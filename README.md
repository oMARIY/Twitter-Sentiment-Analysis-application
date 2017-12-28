# Twitter-Sentiment-Analysis-application
A javaFX project that allows the user to search tweets off of twitter, perform sentiment analysis on them and save them to a databases.
Incredibly useful for investigation and research

The main purpose of this application is to find supplementary evidence on twitter for research and investigation.
The application can perform sentiment anaylsis, which is where text is analyzed for positive and negative connotations.
You can also save your searched tweets to a database, which is useful if you want to perform long term data analysis.
You may also take data from the application else where if you want to perform another type of analysis on it.

There are a few things that might go wrong in the application if not configured properly. First of all, the application uses
twitter authentication which can be obtained from creating a twitter application in the following link - https://apps.twitter.com/
There are four pieces of information you need for the authentication:

  Consumer Key
  Consumer Secret 
  Access Token 
  Access Token Secret
  
These four are currently available in the program, with my authentication details being there. However they will expires soon.
So you may need to change them, when necessary. Once the authentication has been configured you are good to go. The application
itself can collect up to five hundred tweets relating to your search at any one time and you can save several tweets to your
database so that you can have as many tweets as you want collected. Lastly, I will tell about the sentiment analysis. I said 
before how it works, it searches a piece of text for negative and positive connotations. The result of the analysis is a 
score from one to five. The closer to five the score is, the more positive the tweet(s) is and the closer to one the tweet is
the more negative the tweet is. So if you were to get a score of 3, then you can be pretty sure that the tweet(s) are neutral
(50/50 in terms of good and bad opinions). In addition to this, you can take the database outside the application and perform 
your own type of data analysis on it, if you want.

APPLICATION SCREENSHOT
![alt tag](https://github.com/oMARIY/Twitter-Sentiment-Analysis-application/blob/master/Screenshots/Screen%20Shot%202017-12-28%20at%2021.44.49.png)

SEARCHING TWEETS
![alt tag](https://github.com/oMARIY/Twitter-Sentiment-Analysis-application/blob/master/Screenshots/Screen%20Shot%202017-12-28%20at%2021.46.20.png)

SEARCHING TRENDS
![alt tag](https://github.com/oMARIY/Twitter-Sentiment-Analysis-application/blob/master/Screenshots/Screen%20Shot%202017-12-28%20at%2021.57.42.png)
