WireFrame 

User Requirements: 

  Users are prompted to sumbit all of the following information when they click on the Information tab on the MainActivity menu for the first time:
    1. User age 
    2. User gender 
    3. User ethnicity
    4. Child age
    5. Child gender
    6. Child ethnicity (optional, can be null)
    **In order to create an account for the user, information such as a user name and password is necessary; however, account settings for
      the app may not be required.**

Data Handling: 

  The personal information (1-6) of each user shall be stored into a MySQL database in a table called UserInfo with the unique identifier being the phone number
  Once personal information has been submitted, the text becomes read-only even after the app has been closed and opened. 
  ?Should users be allowed to edit their personal informaiton? __They should, but let's not worry about it for now.__
  
  An audio recording for the zoo is stored as a URL to the mp3 file onto the Zoo table; an audio recording for the library is stored onto the Library table
  *The unique identifier from UserInfo is added to recording tables, attaching user info to each user recording and connecting tables within a table*
  *Audio files cannot be sent to server unless all the non-null columns within the UserInfo table have all been completed* (A toast will pop up if the UserInfo table has not been filled with the following text: "Please complete your user information found from the homepage menu before submitting a recording")
  
Mappping/Notifications: 
  
  There are two routes a user takes to get led to a Questionare page:
    Route A: The user opens the app and opens the "Location 123 Questionare" page from the MainActivity
    Route B: The user approaches "Location 123" and a background Map API service runs when the app is in the background or closed, sending a push notification that carries an intent that leads the user to "Location 123 Questionare" page 
      The background service should constantly locate the user relative to a list of coordinates, to determine which push notification to send
      When the user is within 100 meters of the coordinate, a push notification reads "Please fill out 'Location 123 Questionare'"
  
Recording/Submitting

  Users are led to activites that are composed of strings (questions) and three buttons: "Record", "Delete", "Stop + Submit"
  Questions do not have to be retrieved from a database but simply added as strings within each activity's xml file
  *Potential Problem: ?If a user submits two recordings, will the table create two rows for the user submission or will one get deleted?*  __We would prbably want each recording to take a separate row, at least until the previous recording has been fully uploaded to the server.__
  
  
App Flow: 
a. MainActivity:
  The user is navigated to this page when they open the app directly. It will be a scrollview of fragments of each Location Questionare: Clicking on the fragment leads to each Questionare Activity. 
  The menu of the MainActivity has one tab: This tab is called "Information" and clicking on it leads to an activity of text and edittext fields along with a Submit and Edit button in which a users enter their information.
  Users can back out to the MainActivity once again. 
b. Refer to Mapping/Notifications section for how a user lands onto the Location Questionare activity
c. Location Questionare: Once a users submit recordings from this activity, they can terminate the app. 
  
  

  
