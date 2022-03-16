ps -ef | grep ps2-alerts-remind | grep -v grep | cut -c 9-16  | xargs -r kill -9
