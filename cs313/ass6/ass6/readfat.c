//
//  readfat.c
//  ass6
//
//  Created by William You on 2013-03-31.
//  Copyright (c) 2013 William You. All rights reserved.
//

#include <stdio.h>
#include <stdlib.h>
#include <math.h>

# define ENTRY_S 32
# define ENTRY_NAME_S 8
# define ENTRY_EXT_S 3
# define ENTRY_ATTRS_S 1
# define ENTRY_DIR_MASK 0x10
# define FIRST_CLUSTER_OFFSET 26
# define SIZE_OFFSET 28
# define END_OF_ENTRIES 0x00
# define DOT_ENTRY 0x2e
# define ENTRY_DELETED 0xe5
# define FAT_MASK 4096

# define INVALID_CHARS_COUNT 16
static int INVALID_CHARS[16] = { 0x5c, 0x22, 0x2a, 0x2b, 0x2c, 0x2e, 0x2f, 0x3a, 0x3b, 0x3c, 0x3d, 0x3e, 0x3f, 0x5b, 0x5d, 0x7c };

/* Linked list of cluster numbers */
typedef struct cluster {
    unsigned int cluster_n;                 /* the cluster number */
    struct cluster *next;                   /* next cluster number */
} cluster;

/* Informations about the entry */
typedef struct entry_info {
    unsigned char name[8];                  /* name of file or directory */
    unsigned char ext[3];                   /* extension of file */
    unsigned char attrs;                    /* attributes of the file or direcotry */
    unsigned int directory;                 /* is this entry a directory, 1 representing yes */
    unsigned int first_cluster;             /* number of the first cluster containing its data */
    size_t size;                            /* size of file */
    
    cluster *cluster;                       /* a chain of cluster numbers used by this entry */
    struct entry_info *next_entry;          /* a chain of sibling entries */
    struct entry_info *sub_entries;         /* a chain of sub entries */
    
    unsigned int printed;                   /* a boolean represents if this entry has been printed */
} entry_info;

/* Informations about the filesystem */
typedef struct filesystem_info {
    size_t sector_s;                        /* sector size */
    size_t cluster_s;                       /* cluster size in sectors */
    unsigned int reserved_sectors_n;        /* number of reserved sectors */
    unsigned int fat_n;                     /* number of FAT copies */
    unsigned int root_entries_n;            /* number of entries in root directory */
    unsigned int fat_sectors_n;             /* number of sectors per file allocation table */
    unsigned int hidden_sectors_n;          /* number of hidden sectors */
    unsigned int first_fat_sector;          /* sector number of the first copy of the file allocation table */
    unsigned int root_first_sector;         /* sector number of the first sector of the root directory */
    unsigned int first_usable_sector;       /* sector number of the first sector of the first usable data cluster */
    
    entry_info **root_entries;              /* array of root entries */
} filesystem_info;

/*
 Copy n bytes starting from offset o from buffer to output
 */
void copy_bytes(unsigned char *buffer, unsigned char *output, unsigned int n, unsigned int o) {
    unsigned int i;
    for (i=0; i<n; i++) {
        *(output + i) = *(buffer + o + i);
    }
}

/*
 Read n bytes starting at offset o from buffer as little endian number
 */
unsigned int read_little(const unsigned char *buffer, unsigned int o, unsigned int n) {
    unsigned int i, number = 0;
    for (i=0; i<n; i++) {
        number |= buffer[o + i] << (i * 8);
    }
    
    return number;
}

/*
 Check if the name of entry is valid
 */
int check_valid_name(entry_info *entry) {
    int i, j, valid = 1;
    for (i=0; i<INVALID_CHARS_COUNT; i++) {
        unsigned char c = (unsigned char)INVALID_CHARS[i];
        for (j=0; j<8; j++) {
            if (entry->name[j] == c) {
                if (!(entry->directory && (j==0 || j==1) && c == '.')) {
                    valid = 0;
                    break;
                }
            }
        }
    }
    
    return valid;
}

/*
 Read and output the entry information from entry into info
 */
void read_entry_info(unsigned char *entry, entry_info *info) {
    copy_bytes(entry, info->name, ENTRY_NAME_S, 0);
    if ((info->name)[0] == (unsigned char)0x05) {
        (info->name)[0] = (unsigned char)0xe5;
    }
    copy_bytes(entry, info->ext, ENTRY_EXT_S, ENTRY_NAME_S);
    copy_bytes(entry, &(info->attrs), ENTRY_ATTRS_S, ENTRY_NAME_S + ENTRY_EXT_S);
    info->directory = (info->attrs & ENTRY_DIR_MASK) >> 4;
    info->first_cluster = read_little(entry, FIRST_CLUSTER_OFFSET, 2);
    if (!info->directory) {
        info->size = read_little(entry, SIZE_OFFSET, 4);
    }
}

/*
 Read and output the filesystem information from sector into info
 */
void read_filesystem_info(unsigned char *sector, filesystem_info *info) {
    info->sector_s               = read_little(sector, 11, 2);
    info->cluster_s              = sector[13];
    info->reserved_sectors_n     = read_little(sector, 14, 2);
    info->fat_n                  = sector[16];
    info->root_entries_n         = read_little(sector, 17, 2);
    info->fat_sectors_n          = read_little(sector, 22, 2);
    info->hidden_sectors_n       = read_little(sector, 28, 2);
    info->first_fat_sector       = info->reserved_sectors_n + info->hidden_sectors_n;
    info->root_first_sector      = info->first_fat_sector + info->fat_sectors_n * info->fat_n;
    info->first_usable_sector    = info->root_first_sector + info->root_entries_n * ENTRY_S / info->sector_s;
}


/*
 Construct an array representing the FAT
 */
void construct_fat(unsigned char *input, unsigned int *output, unsigned int fat_n) {
    int i, j;
    for (i=2, j=3; i<fat_n / 2; i+=2, j+=3) {
        unsigned int int3 = read_little(input, j, 3);
        unsigned int fat1 = int3 % FAT_MASK;
        unsigned int fat2 = int3 / FAT_MASK;
        output[i] = fat1;
        output[i+1] = fat2;
    }
}

/*
 Construct a tree of sub directories of the entry
 */
void construct_entry(entry_info *entry, unsigned int *FAT, unsigned char *data, filesystem_info *info) {
    if (entry->name[0] == (unsigned char)ENTRY_DELETED) {
        return;
    }
    if (!check_valid_name(entry)) {
        return;
    }
    
    entry->next_entry = NULL;
    entry->sub_entries = NULL;
    cluster *c = (cluster*)malloc(sizeof(cluster));
    c->cluster_n = entry->first_cluster;
    c->next = NULL;
    entry->cluster = c;
    unsigned int fat_entry = FAT[c->cluster_n];
    while (((0x002 <=fat_entry) && (fat_entry <= 0xfef)) || ((0xff8 <= fat_entry) && (fat_entry < 0xfff))) {
        cluster *new_c = (cluster*)malloc(sizeof(cluster));
        new_c->cluster_n = fat_entry;
        new_c->next = NULL;
        c->next = new_c;
        c = new_c;
        
        if ((0xff8 <= fat_entry) && (fat_entry <= 0xfff)) break;
        fat_entry = FAT[c->cluster_n];
    }
    
    if (entry->directory && ((entry->name)[0] != (unsigned char)DOT_ENTRY)) {
        c = entry->cluster;
        entry_info *sub_entries = NULL;
        while (c != NULL) {
            unsigned int cluster_offset = c->cluster_n - 2;
            unsigned char *cluster_ptr = data + cluster_offset * info->cluster_s * info->sector_s;
            unsigned int i;
            
            for (i=0; i<(info->sector_s * info->cluster_s); i+=ENTRY_S) {
                unsigned char *entry_ptr = cluster_ptr + i;
                if (entry_ptr[0] == (unsigned char)END_OF_ENTRIES) break;
                if (entry_ptr[0] == (unsigned char)ENTRY_DELETED) continue;
                
                entry_info *new_entry = (entry_info*)malloc(sizeof(entry_info));
                read_entry_info(entry_ptr, new_entry);
                
                if (sub_entries == NULL) {
                    sub_entries = new_entry;
                    entry->sub_entries = sub_entries;
                }
                else {
                    sub_entries->next_entry = new_entry;
                    sub_entries = new_entry;
                }
                
                // Recursively construct sub-entries
                construct_entry(new_entry, FAT, data, info);
            }
            c = c->next;
        }
        
    }
}

/*
 Print entry and the sub-entries informations in terminal
 */
void print_entry_info(entry_info *info, char *prepend, unsigned int prepend_length) {
    
    // Do not print entry again
    if (info->printed == 1) {
        return;
    }
    info->printed = 1;
    
    // Update prepend string
    unsigned int child_prepend_length = prepend_length + 1;
    char *child_prepend = (char*)malloc(sizeof(char) * child_prepend_length);
    sprintf(child_prepend, "%s\\", prepend);
    
    // Print file name with extension
    printf("Filename: %s\\", prepend);
    int i;
    for (i=0; i<ENTRY_NAME_S; i++) {
        if (info->name[i] == ' ') break;
        printf("%c", info->name[i]);
        
        // Update prepend string
        child_prepend_length++;
        sprintf(child_prepend, "%s%c", child_prepend, info->name[i]);
    }
    if (info->ext[0] != ' ') {
        printf(".");
        for (i=0; i<ENTRY_EXT_S; i++) {
            if (info->ext[i] == ' ') break;
            printf("%c", info->ext[i]);
        }
    }
    printf("\n");
    
    // Print if this is a directory
    if (info->directory) {
        printf("This file is a directory.\n");
    }
    // Print size of file if this is a file
    else {
        printf("Size: %lu\n", info->size);
    }
    
    // Print the clusters
    printf("Clusters: ");
    cluster *c = info->cluster;
    while (c) {
        printf("%u ", c->cluster_n);
        c = c->next;
    }
    printf("\n");
    printf("\n");
    
    // Print the sibling entries
    entry_info *sibling = info->next_entry;
    while (sibling != NULL) {
        print_entry_info(sibling, prepend, prepend_length);
        sibling = sibling->next_entry;
    }
    
    // Print the sub entires
    if (info->sub_entries != NULL) {
        print_entry_info(info->sub_entries, child_prepend, child_prepend_length);
    }
}

/*
 Print filesystem information in terminal
 */
void print_filesystem_info(filesystem_info *info, unsigned int root_entries_count) {
    printf("Sector size: %lu\n", info->sector_s);
    printf("Cluster size in sectors: %lu\n", info->cluster_s);
    printf("Root directory size (nb of entries): %i\n", info->root_entries_n);
    printf("Sectors per fat: %i\n", info->fat_sectors_n);
    printf("Reserved sectors: %i\n", info->reserved_sectors_n);
    printf("Hidden sectors: %i\n", info->hidden_sectors_n);
    printf("Fat offset in sectors: %i\n", info->first_fat_sector);
    printf("Root directory offset in sectors: %i\n", info->root_first_sector);
    printf("First usable cluster offset in sectors: %i\n", info->first_usable_sector);
    printf("\n");
    
    unsigned int i;
    for (i=0; i<root_entries_count; i++) {
        print_entry_info(info->root_entries[i], "", 0);
    }
}

/*
 Usage: pass in file path of filesystem.dat as argument
 */
int main(int argc, const char * argv[]) {
    
    unsigned int i, root_entries_count = 0;
    
    if (argc != 2) {
        printf("Invalid number of arguments."); return -1;
    }
    
    FILE *dat = fopen(argv[1], "r");
    if (dat == NULL) {
        perror((char*)dat); return -1;
    }
    
    fseek(dat, 0x0, SEEK_END);
    size_t data_s = ftell(dat);
    rewind(dat);
    
    // Part 1
    unsigned char *root_sector = (unsigned char*)malloc(sizeof(unsigned char) * 512);
    fread(root_sector, 512, 1, dat);
    filesystem_info *info = (filesystem_info*)malloc(sizeof(filesystem_info));
    read_filesystem_info(root_sector, info);
    free(root_sector);
    
    // Skip reserved bytes
    char *skip = (char*)malloc(sizeof(char) * (info->reserved_sectors_n * info->sector_s - 512));
    fread(skip, info->reserved_sectors_n * info->sector_s - 512, 1, dat);
    free(skip);
    
    // Get all copies of FAT into one array
    unsigned char *fat_char = (unsigned char*)malloc(sizeof(unsigned char) * info->fat_n * info->fat_sectors_n * info->sector_s);
    fread(fat_char, info->fat_sectors_n * info->sector_s, info->fat_n, dat);
    unsigned int *FAT = (unsigned int*)malloc(sizeof(unsigned int) * info->fat_sectors_n * info->sector_s * 8 / 12);
    construct_fat(fat_char, FAT, (unsigned int)(info->fat_sectors_n * info->sector_s * 8 / 12));
    
    // Part 2, insert all root entries into filesystem_info
    entry_info ** root_entries = (entry_info**)malloc(sizeof(entry_info*) * info->root_entries_n);
    info->root_entries = root_entries;
    
    unsigned char *root_entry = (unsigned char*)malloc(sizeof(unsigned char) * ENTRY_S);
    for (i=0; i<info->root_entries_n; i++) {
        fread(root_entry, ENTRY_S, 1, dat);
        if (root_entry[0] != (char)END_OF_ENTRIES) {
            entry_info *e_info = (entry_info*)malloc(sizeof(entry_info));
            read_entry_info(root_entry, e_info);
            if (root_entry[0] != (unsigned char)ENTRY_DELETED) {
                info->root_entries[root_entries_count] = e_info;
                root_entries_count++;
            }
        }
    }
    free(root_entry);
    
    // Read the data clusters into a buffer
    data_s -= info->first_usable_sector * info->sector_s;
    unsigned char *data = (unsigned char*)malloc(sizeof(unsigned char) * data_s); // Data starts at cluster 2
    fread(data, data_s, 1, dat);
    
    // Gather informations about files and subdirectories
    for (i=0; i<root_entries_count; i++) {
        construct_entry(info->root_entries[i], FAT, data, info);
    }
    
    // Output all information about the file system
    print_filesystem_info(info, root_entries_count);
    
    
    // Clean up
    free(data);
    
    for (i=0; i<root_entries_count; i++) {
        free(info->root_entries[i]);
    }
    free(root_entries);
    free(info);
    return 0;
}
