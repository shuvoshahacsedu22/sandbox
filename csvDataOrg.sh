# Random nasty Unix commands to separating out team data into $team-opponent games, and for merging all into one file.

# get the data for each game in its own csv file titled csv-files/$team-$oponent.csv
for team in `ls data`; do for oponent in `ls data/$team`; do cat data/$team/$oponent/*.csv | tail +2 | sed '/^$/d' > csv-files/$team-$oponent.csv; done; done


# better!
for team in `ls data`; do for oponent in `ls data/$team`; do tail +2 data/$team/$oponent/*.csv  | sed 's/==>.*<==//g' | sed '/^$/d' > csv-files/$team-$oponent.csv; done; done


# merge all of the above csv files into one large file
for f in `ls`; do cat $f >> ../all-data.csv; done

