#!/bin/bash
if [[ "$TRAVIS_BRANCH" == "master" ]]; then
DIR=$(dirname $0)
echo $DIR
RELEASE_NOTES=$(git log --format="%cn @ "$TRAVIS_BRANCH"
------
%B" -n 1 $TRAVIS_COMMIT)
echo "$RELEASE_NOTES" > $DIR/../crashlytics_release_notes.txt
./gradlew :app:crashlyticsUploadDistributionDebug
fi