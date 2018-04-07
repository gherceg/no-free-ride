# No Free Ride

An Android application that helps users split the costs of owning/maintaining/driving a car. Whether you're a driver
that wants your friends to stop freeloading off of your car, or a frequent passenger looking to chip in, this will help.
At the bare minimum it supports gas costs, but insurance, maintenance, and parking can all be added if desired. 

<img src="https://raw.githubusercontent.com/gherceg/no-free-ride/master/screenshots/pixel_2_xl_screen.png" width="400">

# Current Features
Uses Android's LocationServices to track user's location for the duration of the drive.
Runs a foreground service to allow user to continue using their phone as they would, while also being aware of location tracking.
Uses Google Maps API to draw the drive route once complete.  


# Future Additions

While the existing polyline works well (a location must be within a certain accuary threshold to be added), the next improvement is using Google Maps Roads API to snap points to the road resulting in a more accurate distance calculation. 

Using myGasFeed API to determine nearby gas prices based on location.
