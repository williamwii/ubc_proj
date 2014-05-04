#include <stdint.h>

#include "stp.h"
#include "wraparound.h"

/* Adds the specified numbers considering wraparound. Note that this
 * is done automatically by C simply by using the uint16_t type.
 */
uint16_t plus(uint16_t val1,  uint16_t val2) {
  
  return val1 + val2;
}

/* Subtracts the specified numbers considering wraparound. Note that
 * this is done automatically by C simply by using the uint16_t
 * type.
 */
uint16_t minus(uint16_t val1, uint16_t val2) {
  
  return (val1 - val2);
}

/* Returns 1 if val1 is greater than val2 in a wraparound
 * scenario. This is done by identifying in which direction the
 * distance is shorter, i.e., if the distance between the numbers is
 * shorter through the wraparound, the computation is done through
 * it. The minus function is used to obtain the distance in each
 * direction. If the distance from val2 up to val1 (val1-val2) is
 * shorter than the distance from val1 up to val2 (val2-val1), then
 * val1 is greater than val2. If both numbers are the same, both minus
 * calls return zero, which would result in false, as expected. This
 * function will return false if the numbers are equally distant from
 * each other (e.g. 0 and 32768).
 */
int greater(uint16_t val1, uint16_t val2) {
  
  return minus(val2, val1) > minus(val1, val2);
}


