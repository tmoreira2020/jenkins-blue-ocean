# Jenkins Blue Ocean

This project demonstrates how to create from the scratch a Jenkins instance configured with Blue Ocean.

## Build
To build the docker image run this command from the root directory of this project `docker build . -t tmoreira2020/jenkins-blue-ocean:local`

## Run
To run you must first grab a [github token](https://github.com/settings/tokens) from your account and replace the `$mytoken` value in the command `docker run -p 8080:8080 -e GITHUB_TOKEN=$mytoken tmoreira2020/jenkins-blue-ocean:local` then just fire it up.

## Enjoy