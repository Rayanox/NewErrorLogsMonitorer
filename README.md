# NewErrorLogsMonitorer
A program which constantly scans the logs on an environment (PREPROD for my purpose), stores all the logs and alerts when a new error log is detected. This helps doing no regression tests through an autoscan.

## Current purpose

Currently, the program is able to scan the logs of a PREPROD environment and to store them in a database. It is also able to detect new error logs and to send an email to the IT service to alert them. The program is also able to detect when your environments (PREPROD now) has a regression and to send an email to the IT service to alert them.

[![TNR Tool](https://raw.githubusercontent.com/Rayanox/NewErrorLogsMonitorer/main/Image-dashboard.JPG)](https://youtu.be/CEx09YiQbDk "TNR tool")

## Next evolution
This program is involving and is intented to become a central tool for the IT service (usefull dashboard for following processes and starting actions).

### Help you at work (IT management and process organization helper)

This program is usefull for Lead developpers, project chiefs, IT service chiefs, Product owner (if they are highly attached to the IT service), ect. It is designed to be the central tool of the IT service to help improve the productivity of the team by displaying strategics dashboard, alert when developpers when a regression occurs, give the hand to product owners to avoid asking developpers to go on the next deployment step, etc.
The main purpose here is to help the IT managers to ease their work with automations.

## Feel free to contribute

This project is open source, so feel free to contribute to it. I will be happy to see your pull requests and to discuss with you about the project. I've already created some issues that we need, so don't hesitate to assign yourself if you also are motivated like I am. We'll be more efficient together !
