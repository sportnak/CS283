To test this file you run the server file and open netcat.

Then you enter "REGISTER " followed by your username and password.
You can then type "HELP" to view all of the commands listed, but in case I left any out, here they are.

HELP - displays commands

REGISTER + username + password - registers a user, puts their password in. If user is logged on, it tells them someone is already logged on, if the user is logged on somewhere else, it doesn't register them.

UNREGISTER - "logs them out" they cannot send or receive messages.

SEND - Send to all
SEND TO username - Send to user
SEND TO GROUP groupname - sends to groupname

GROUP - joins a group

Leave GROUP + groupname - leave said group

SHUTDOWN - notifies all users of server shutdown, closes server.

MYGROUPS - displays all the groups the user is a part of.

POLL - if user received a message while offline, they can use poll to see their messages.

ACK - used when polling to acknowledge reception.