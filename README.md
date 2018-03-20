# README #

## Build jar with Maven ##
mvn assembly:assembly -DdescriptorId=jar-with-dependencies package

## Install Terraform ##

### Download and install AWS CLI (if not already) ###
* https://aws.amazon.com/cli/

### Download and install terraform ###
* https://www.terraform.io/downloads.html
* add to $PATH

## Set Your AWS Credentials ##
* Create an IAM User in your AWS account that you can use to provision

## Build Infrastructure ##

### Initialise the working directory
* cd {repo_location}/fizzbuzz-lambda/IaC/terraform/fizzbuzz
* terraform init

### Deploy to AWS ###
* cd {repo_location}/fizzbuzz-lambda/IaC/terraform/fizzbuzz
* terraform plan
* terraform apply

### Delete ###
* terraform destroy


------------------------------------
# Notes for articles #

## 1. Create an Alexa Skill ##

## 2. Create the Lambda ##

## 3. Deploy the Lambda using Terraform ##

## 4. Deploy the Lambda using CloudFormation ##
