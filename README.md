# JIMCOM
========
(Semestral project for PR2 on FEL@CVUT.)

JIMcom is simple java Instant Messenger. It uses server-less topology (NAT restricted). All local settings will be stored in plaintext in file. 

###Current state summary
When user defines his ID, main app is started. Incomming connection listener is started and all known contacts starts atempting for connections to their targets. When incomming connection of unknown ID is received, contact is automaticaly added to contact list. Known contacts are modifiable as well as local ID. All setting and contacts are stored at exit.

### progress
|objective|ststus|note|
|---------|------|----|
|schedule project|DONE||
|create main structure|DONE||
|user identification|DONE|nick & password hash paired|
|network protocol|DONE|test and define|
|storing/loading contacts and messages|DONE||
|basic chat|DONE|verified - working|
|base GUI|DONE||
|add new contact GUI|IP||
|local identification GUI|IP||
|file transfers|OH|including hash for integrity checking|


*OH - On Hold*
*IP - In Progress*


### additional features
(most won't be implemented due to insufficient time)
|feature|status|note|
|----|---|---|
|keyboard shortcuts|optional||
|crypted messaging|optional||
|image previewing|optional|if received file is image, offer preview in chat widow|
|minimize to status panel|optional||
|play notification sounds|optional||
|multichat|optional||
|history sharing|optional|history of chat may be requested from oponent|

