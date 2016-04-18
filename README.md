# Event_Garden

Main emulator for development right now: Nexus 4


KNOWN ISSUES:
  - App will crash if event details are not fully filled out (Time/Date?)
  - Event names do not display full event name on list view.
  - Map markers have no interactivity
  - Cannot add event based on location on map
  - Map makers are are destroyed and created again on switching from/to the map tab
  - It would probably be a good idea to create a database class that stores all the events to fake the database hookup.
  - Address validation should be in the create event page as well.


Class rundown:

Event: 
  - Stores information about the events stored in the app.

Filter:
  -

Add Event:
  - Activity for adding an event to the application
  
List_Fragment:
  - Basically the tab for the list view

List_Adapt:
  - This is the "container" that holds events on the list view and organizes them.
  
LogIn:
  - The activity associated with the log in screen.

Map_Fragment
 - This is the tab for the Map view

Profile
  - Activity associated with profile.


