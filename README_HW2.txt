The benchmark client is the same as the simple client. The only difference is that when the benchmark client is used, we took a timestamp at the beginning of the connection and then ran a while loop that ran while the current time was less than 500 milliseconds longer than the start. On each iteration, we incremented a counter variable and then returned that value divided by the 500 milliseconds to get the number of users serviced per millisecond.

The Multi-threaded server serviced a considerably larger number of users per millisecond than the simple server based on the fact that the multithreaded server could service multiple clients at the same time.


