package sample;


import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;

import javafx.collections.ObservableList;
import javafx.scene.control.*;

import javafx.scene.paint.Color;
import javafx.util.Callback;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import java.io.IOException;

import java.sql.*;
import java.util.*;



public class Controller <T> {
    @FXML
    private TextField searchTweetsInput;
    @FXML
    private TextField searchTrendsInput;
    @FXML
    private TextArea tweetInfo;
    @FXML
    private Button searchTweetsBtn;
    @FXML
    private Button searchTrendsBtn;
    @FXML
    private ListView TweetsListView;
    @FXML
    private ChoiceBox<Integer> numsTweets;
    @FXML
    private Button closeBtn;
    @FXML
    private TextField filterTweetsInput;
    @FXML
    private Button filterTweetsBtn;
    @FXML
    private Button getAllTwtBtn;
    @FXML
    private Button addToDBbtn;
    @FXML
    private Button sentimentBtn;

    private double average;
    private int numsOfTweets;
    private Tweet tweet;
    private Twitter twitter;
    private AlertBox alertBox;
    private NPL npl = new NPL();
    private Runnable runnable;
    private String sentiment;
    private Connection conn;
    private int count;
    private boolean isRetweeted;


    private final String CREATE_TABLE_QUERY_TWEETS = "CREATE TABLE IF NOT EXISTS tweets (TweetID integer, UserName text, Tweet text, RetweetCount integer," +
            "LikeCount integer, FollowerCount integer, FriendCount integer, Date text, Retweeted boolean, Location text)";
    private final String CREATE_TABLE_QUERY_TWEET_SEARCHES = "CREATE TABLE IF NOT EXISTS searches (SearchID integer, Search_Term text)";
    private final String QUERY_ADD_CURRENT_ITEMS = "INSERT INTO tweets (TweetID, UserName, Tweet, RetweetCount, LikeCount, FollowerCount, " +
            "FriendCount, Date, Retweeted, Location) VALUES(?,?,?,?,?,?,?,?,?,?)";
    private final String QUERY_ADD_SEARCH_ITEMS = "INSERT INTO searches (SearchID, Search_Term) VALUES(?,?)";


    //intialize method
    @FXML
    public void initialize() {
        numsTweets.getSelectionModel().selectFirst();
    }


    //Twitter Code
    public Twitter Connect() {
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true).
                setOAuthConsumerKey("M1bzSpIVCaP1Q2r1TM69SSRaB").
                setOAuthConsumerSecret("QdJL60d7BZyP9Cflw52xa7RrEv9xaUQzPpRwRxxy19A9Bqsgli").
                setOAuthAccessToken("2372196459-6fA4t8gAOYvrH4G19uflMSYeonJSarsA2w5IYbZ").
                setOAuthAccessTokenSecret("fsb1pbKsjIS4eX5iN6zgD9BWigIlCk0PSD1EOnYApoIzM");

        TwitterFactory tf = new TwitterFactory(cb.build());


        return tf.getInstance();
    }


    public List<Status> SearchingTwitter(String searchInput) {
        searchInput = searchTweetsInput.getText();
        Query query = new Query(searchInput);

        if (numsTweets.getSelectionModel().getSelectedItem().equals("100")) {
            numsOfTweets = 100;
        } else if (numsTweets.getSelectionModel().getSelectedItem().equals("200")) {
            numsOfTweets = 200;
        } else if (numsTweets.getSelectionModel().getSelectedItem().equals("300")) {
            numsOfTweets = 300;
        } else if (numsTweets.getSelectionModel().getSelectedItem().equals("400")) {
            numsOfTweets = 400;
        } else if (numsTweets.getSelectionModel().getSelectedItem().equals("500")) {
            numsOfTweets = 500;
        } else {
            numsOfTweets = 100;
        }

        long id = Long.MAX_VALUE;
        List<Status> tweets = new ArrayList<>();
        while (tweets.size() < numsOfTweets) {
            if (numsOfTweets - tweets.size() > 100) {
                query.setCount(100);
            } else {
                query.setCount(numsOfTweets - tweets.size());
            }
            try {

                QueryResult result = Connect().search(query);
                tweets.addAll(result.getTweets());
                System.out.println("Gathered " + tweets.size() + " tweets");
                for (Status t : tweets) {
                    if (t.getId() < id) {
                        id = t.getId();
                    }


                }
            } catch (TwitterException te) {
                te.printStackTrace();
            }
            query.setMaxId(id - 1);
        }


        return tweets;
    }

    public void setTweetsListView() {
        TweetsListView.setCellFactory(new Callback<ListView<T>, ListCell<T>>() {
            @Override
            public ListCell<T> call(ListView<T> param) {
                ListCell<T> cell = new ListCell<T>() {
                    @Override
                    protected void updateItem(T item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setText(null);
                        } else {
                            if(item instanceof Tweet){
                                setText(((Tweet)item).getTweet());
                                if (((Tweet)item).isRetweeted()) {
                                    setTextFill(Color.GREEN);
                                } else {
                                    setTextFill(Color.BLACK);
                                }
                            }
                            if(item instanceof String){
                                setText((String) item);
                            }

                        }
                    }
                };

                return cell;
            }
        });

        TweetsListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                if (newValue != null) {
                        Tweet fullTweet = (Tweet) TweetsListView.getSelectionModel().getSelectedItem();
                        String info = "Author of Tweet: " + fullTweet.getUserName() + "\n" +
                                "Full Tweet: " + fullTweet.getTweet() + "\n" +
                                "Number of Retweets: " + fullTweet.getRetweetCount() + "\n" +
                                "Retweeted: " + fullTweet.isRetweeted() + "\n" +
                                "Number of Likes: " + fullTweet.getLikeCount() + "\n" +
                                "Number of Followers: " + fullTweet.getFollowerCount() + "\n" +
                                "Number of Friends: " + fullTweet.getFriendsCount() + "\n" +
                                "Date the Tweet was Posted: " + fullTweet.getDate() + "\n" +
                                "Location of Tweet: " + fullTweet.getLocation();
                        tweetInfo.setText(info);

                }
            }
        });
    }


    @FXML
    public void tweetSearch() {
        List<Status> tweets = SearchingTwitter(searchTweetsInput.getText());
        ObservableList<Tweet> tweetList = FXCollections.observableArrayList();
        try {
            for (Status t : tweets) {

                tweet = new Tweet(t.getText(), t.getRetweetCount(), t.isRetweet(), t.getFavoriteCount(), t.getCreatedAt().toString(),
                        t.getUser().getName(), t.getUser().getFollowersCount(), t.getUser().getFriendsCount(),
                        t.getUser().getLocation());
                tweetList.add(tweet);

            }
        } catch (NullPointerException e1) {
            System.out.println("Something went wrong with loading twitter data - " + e1.getMessage());
            e1.printStackTrace();
        }


        TweetsListView.setItems(tweetList);
        setTweetsListView();

    }

    Runnable getallPreviousSearches = new Runnable() {
        @Override
        public void run() {
            conn = connectToDB();
            ObservableList<Tweet> searches = FXCollections.observableArrayList();
            try (Statement statement = conn.createStatement();
                 ResultSet resultSet = statement.executeQuery("SELECT Search_Term FROM searches ORDER BY Search_Term COLLATE NOCASE")) {
                while (resultSet.next()) {
                    String search_terms = resultSet.getString(1);
                    tweet = new Tweet();
                    tweet.setTweet(search_terms);
                    searches.add(tweet);
                }
                if(TweetsListView.getItems().size() != 0){
                    TweetsListView.getItems().removeAll();
                }
                TweetsListView.setItems(searches);
                TweetsListView.setCellFactory(new Callback<ListView, ListCell>() {
                    @Override
                    public ListCell call(ListView param) {
                        ListCell<Tweet> cell = new ListCell<Tweet>(){
                            @Override
                            protected void updateItem(Tweet item, boolean empty) {
                                super.updateItem(item, empty);
                                if(empty){
                                    setText(null);
                                }
                                else{
                                    setText(item.getTweet());
                                    setTextFill(Color.BLACK);
                                }
                            }
                        };
                        return cell;
                    }
                });

            } catch (SQLException e) {
                System.out.println("Something went wrong getting previous searches from the database - " + e.getMessage());
            }
        }
    };

    @FXML
    public void getPreviousSearches() {
        Platform.runLater(getallPreviousSearches);

    }

    //Sentiment Analysis
    public void sentimentAnalysis() throws IOException {
        if (TweetsListView.getItems().size() != 0) {
            List<Tweet> sentimentTweets = new ArrayList<>(TweetsListView.getItems().size());
            for (int i = 0; i < TweetsListView.getItems().size(); i++) {
                Tweet tweet = (Tweet) TweetsListView.getItems().get(i);
                sentimentTweets.add(tweet);
            }
            npl.init();
            StringBuilder info = new StringBuilder();
            double sum = 0;

            for (Tweet t : sentimentTweets) {
                int sentimentScore = npl.findSentiment(t.getTweet());

                sum += Double.valueOf(sentimentScore);

            }
            average = sum / sentimentTweets.size();
            if (average >= 0 && average <= 1) {
                sentiment = "Very Negative";
            } else if (average >= 1 && average <= 2) {
                sentiment = "Negative";
            } else if (average >= 2 && average <= 3) {
                sentiment = "Neutral";
            } else if (average >= 3 && average <= 4) {
                sentiment = "Positive";
            }


            info.append("The average Sentiment Score is: " + String.valueOf(average) + " - " + sentiment);
            tweetInfo.setText(info.toString());


        } else {
            alertBox = new AlertBox();
            alertBox.display("Error", "There are no tweets to do Sentiment Analysis on.\n" +
                    "Search some tweets or Load them in from the database");
        }

    }

    @FXML
    public void sentimentAnalysisMethod() {
        runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    sentimentAnalysis();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        Platform.runLater(runnable);
    }

    //Database code
    public Connection connectToDB() {
        conn = null;
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:/Users/Ismael/documents/workspace/TweetCatcher/tweets.db");
            Statement statement = conn.createStatement();
            statement.execute(CREATE_TABLE_QUERY_TWEETS);
            statement.execute(CREATE_TABLE_QUERY_TWEET_SEARCHES);
            return conn;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    Runnable filterTwtsRunnable = new Runnable() {
        @Override
        public void run() {
            int listViewSize = TweetsListView.getItems().size();
            ObservableList<Tweet> filteredList = FXCollections.observableArrayList();
            if(listViewSize == 0){
                alertBox = new AlertBox();
                alertBox.display("Application Message", "There are no tweets in the list to filter.\n" +
                        "Try to search some or load some in from the database");
            }

            for(int i = 0; i <listViewSize; i++){
                Tweet currentItem = (Tweet) TweetsListView.getItems().get(i);

                String currentTweet = currentItem.getTweet();

                if(currentTweet.contains(filterTweetsInput.getText())){
                   filteredList.add(currentItem);
                }
            }
            if(filteredList.size() == 0){
                alertBox = new AlertBox();
                alertBox.display("Application Message", "No tweets were found that match your search.\n" +
                        "Try to search something else");
            } else{
                TweetsListView.setItems(filteredList);
                setTweetsListView();
            }
        }
    };
    Runnable addTweetsToDb = new Runnable() {
        @Override
        public void run() {
            Connection connect = connectToDB();
            int listViewSize = TweetsListView.getItems().size();
            List<Tweet> currentItems = new ArrayList<>(listViewSize);

            for (int i = 0; i < listViewSize; i++) {
                Tweet t = (Tweet) TweetsListView.getItems().get(i);
                currentItems.add(t);
            }
            for (Tweet t : currentItems) {
                try (PreparedStatement queryAllItems = connect.prepareStatement(QUERY_ADD_CURRENT_ITEMS)) {
                    queryAllItems.setInt(1, tableRowCount("tweets"));
                    queryAllItems.setString(2, t.getUserName());
                    queryAllItems.setString(3, t.getTweet());
                    queryAllItems.setInt(4, t.getRetweetCount());
                    queryAllItems.setInt(5, t.getLikeCount());
                    queryAllItems.setInt(6, t.getFollowerCount());
                    queryAllItems.setInt(7, t.getFriendsCount());
                    queryAllItems.setString(8, t.getDate());
                    queryAllItems.setBoolean(9, t.isRetweeted());
                    queryAllItems.setString(10, t.getLocation());

                    queryAllItems.execute();


                } catch (SQLException e) {
                    System.out.println("Something went wrong adding tweets (database):  " + e.getMessage());
                    e.printStackTrace();
                }


           }
            System.out.println("Successfully added Tweets to database");

        }
    };
    Runnable getAllTweetsDB = new Runnable() {
        @Override
        public void run() {
            conn = connectToDB();
            ObservableList<Tweet> DBList = FXCollections.observableArrayList();
            List<Tweet> parseList = new ArrayList<>();

            try (Statement statement = conn.createStatement();
                 ResultSet result = statement.executeQuery("SELECT * FROM tweets");) {
                while (result.next()) {
                    String tweetText = result.getString("Tweet");
                    String userName = result.getString("UserName");
                    int RetweetCount = result.getInt("RetweetCount");
                    int LikeCount = result.getInt("LikeCount");
                    int FollowerCount = result.getInt("FollowerCount");
                    int FriendsCount = result.getInt("FriendCount");
                    String Date = result.getString("Date");
                    int retweeted = result.getInt("Retweeted");
                    if(retweeted == 1){
                        isRetweeted = true;
                    } else{
                        isRetweeted = false;
                    }
                    String loc = result.getString("Location");
                    if(loc.isEmpty()){
                        loc = "No Location Recorded";
                    }

                    tweet = new Tweet();
                    tweet.setTweet(result.getString("Tweet"));
                    tweet.setRetweetCount(RetweetCount);
                    tweet.setRetweeted(isRetweeted);
                    tweet.setLikeCount(LikeCount);
                    tweet.setDate(Date);
                    tweet.setUserName(userName);
                    tweet.setFollowerCount(FollowerCount);
                    tweet.setFriendsCount(FriendsCount);
                    tweet.setLocation(loc);

                    DBList.add(tweet);
                }

                TweetsListView.setItems(DBList);
                for(int i = 0; i < TweetsListView.getItems().size(); i++){
                    Tweet b = (Tweet) TweetsListView.getItems().get(i);
                }

                System.out.println("Retrieved All records");
               setTweetsListView();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            }
        };
    Runnable getTrendsFromTwitter = new Runnable() {
        @Override
        public void run() {
            twitter = Connect();
            try {
                String trending = searchTrendsInput.getText();
                ResponseList<Location> locations;
                locations = twitter.getAvailableTrends();
                Integer idTrendLocation = getTrendLocationId(trending);

                if (idTrendLocation == null) {
                    System.out.println("Trend Location Not Found");

                }

                Trends trends = twitter.getPlaceTrends(idTrendLocation);
                ObservableList<String> trendList = FXCollections.observableArrayList();
                for (int i = 0; i < trends.getTrends().length; i++) {
                    String s = trends.getTrends()[i].getName();
                    trendList.add(s);
                }
                TweetsListView.setItems(trendList);
                setTweetsListView();


            } catch (TwitterException e) {
                e.printStackTrace();
            }

        }
    };
        @FXML
        public void trendingTweets(){
            getTrendsFromTwitter.run();
        }
        @FXML
        public void filterTweets(){
        filterTwtsRunnable.run();
    }

        @FXML
        public void getAllTweetsFromDB() {
            Platform.runLater(getAllTweetsDB);
        }

        @FXML
        public void addTweetsToDB() {
            addTweetsToDb.run();
        }

        public int tableRowCount(String tableName) {
            conn = connectToDB();
            if (tableName == "tweets") {
                try (PreparedStatement getCount = conn.prepareStatement("SELECT COUNT(?) AS total FROM tweets")) {

                    getCount.setString(1, "Tweet");
                    ResultSet resultSet = getCount.executeQuery();
                    while (resultSet.next()) {
                        count = resultSet.getInt("total");
                        count += 1;
                    }
                    resultSet.close();
                } catch (SQLException e) {
                    System.out.println("Something went wrong while getting the count of tweets - " + e.getMessage());
                    e.printStackTrace();
                }
            } else if (tableName == "searches") {
                try (PreparedStatement getCount = conn.prepareStatement("SELECT COUNT(?) AS total FROM searches")) {
                    getCount.setString(1, "Search_Term");
                    ResultSet resultSet = getCount.executeQuery();
                    while (resultSet.next()) {
                        count = resultSet.getInt("total");
                        count += 1;
                    }
                    resultSet.close();
                } catch (SQLException e) {
                    System.out.println("Something went wrong while getting the count of searches - " + e.getMessage());
                    e.printStackTrace();
                }
            }
            return count;
        }

        private Integer getTrendLocationId(String Locationname){

            int idTrendLocation = 0;
            try{

                twitter = Connect();

                ResponseList<Location> locations;
                locations = twitter.getAvailableTrends();

                for (Location location : locations) {
                    if (location.getName().toLowerCase().equals(Locationname.toLowerCase())) {
                        idTrendLocation = location.getWoeid();
                        break;
                    }
                }

                if (idTrendLocation > 0) {
                    return idTrendLocation;
                }
            }catch (TwitterException te) {
                te.printStackTrace();
                System.out.println("Failed to get trends: " + te.getMessage());
                return null;
            }
            return idTrendLocation;
        }

}

