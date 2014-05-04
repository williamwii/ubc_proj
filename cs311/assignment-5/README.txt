Philip Storey
94300076
l9n7

Wei You
77610095
r9e7

We have read and complied with the collaboration policy.
We added error messages as per the requirements for bonus mark 1.

Sequential Tests
[Simple]
Open Survey
Type in ‘Yes’
Press Submit
Type in ‘Italy’
Press Submit
Type in ‘2’
Press Submit
Press Okay
Type in ‘Yes’
Press Submit

We should see:
Your Answers
Are you planning to travel soon? Yes
What country are you planning to travel? Italy
How many people are accompanying you? 2
We offer Basic Saving Plan of 5% discount for people travelling in groups. This is your promotion code xxxxx <- where each x is a random integer
Would you like to subscribe our newsletter? Yes

This test was successful.

[Error]


[Back]
Open Survey
Type in ‘Yes’
Press Submit
Type in ‘Italy’
Press Submit
Press Back in browser
Press Back in browser
Type in ‘No’
Press Submit
Type in ‘Yes’
Press Submit

We should see:
Are you planning to travel soon? No
Would you like to subscribe our newsletter? Yes

This test was successful.

[Duplication]
Open Survey
Type in ‘Yes’
Press Submit
Duplicate Tab in browser
Complete original survey with ‘Italy’, ‘4’, ‘Yes’, ‘Yes’
Complete duplicate survey with ‘Norway’, ‘No’, ‘0’, ‘No’

Original Survey should show:
Your Answers
Are you planning to travel soon? Yes
What country are you planning to travel? Italy
How many people are accompanying you? 4
We offer 30% discount for those travelling with 4 or more people to Italy. Are you interested? Yes
Would you like to subscribe our newsletter? Yes

Duplicate Survey should show:
Your Answers
Are you planning to travel soon? Yes
What country are you planning to travel? Norway
Are you also interested in travelling to Italy? No
How many people are accompanying you? 0
Would you like to subscribe our newsletter? No

This test was successful.

[Error]
Open Survey
Type in ‘NEVER!!!!!!!’
Press Submit

We should see
Error
Error! You entered the invalid answer "NEVER!!!!!!!”. Please press back and enter Yes/No.

This test was successful.

1. With a global mutable state hash table, answers given are not deleted when the user presses back on the browser, nor are they overwritten. Answers will just accumulate and when the survey is complete, it will output a page with all answers given, whether or not they were from the specific sequence the user inputted last.

2. Open the base survey url, type in ‘no’, press submit, press back in your browser, type in ‘yes’, pres submit, press back in your browser, type in ‘no’, press submit. Every time you submit something new for the first question, you are accessing the same continuation created by send/suspend.

3. Open the base survey url, close tab. A continuation is created by the inital call to question-one/k. If you do not ever submit anything, it is never invoked.

4. I think it is super exciting to be able to make racket code web pages. I’m sure it will impress all of my friends!