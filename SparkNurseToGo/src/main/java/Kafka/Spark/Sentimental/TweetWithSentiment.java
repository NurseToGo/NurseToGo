package Kafka.Spark.Sentimental;

/**
 * Created by Mayanka on 21-Jul-15.
 * Modified by hastimal on 10/23/2015.
 * Reference : https://github.com/shekhargulati/day20-stanford-sentiment-analysis-demo
 */
public class TweetWithSentiment {

    private String line;
    private String cssClass;
    private String lang;

    public TweetWithSentiment() {
    }

    public TweetWithSentiment(String line, String cssClass) {
        super();
        this.line = line;
        this.cssClass = cssClass;
          }

    public String getLine() {
        return line;
    }

    public String getCssClass() {
        return cssClass;
    }

    @Override
    public String toString() {
        return "Kafka.Spark.Sentimental.TweetWithSentiment [line=" + line + ", cssClass=" + cssClass + "]";
    }

}
