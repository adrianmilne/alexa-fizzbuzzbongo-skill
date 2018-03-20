#Optional - if you want to provide credentials in this file, and not pick up from your environment
#provider "aws" {
#  access_key = "INSERT_ACCESS_KEY"
#  secret_key = "INSERT_SECRET_KEY"
#  region     = "us-east-1"
#}

module "fizzbuzz" {
    source = "../modules/s3-lambda-ddb"

    fizzbuzz_lambda_function_name = "alexa-fizzbuzzbongo-skill"
    fizzbuzz_lambda_object_key = "alexa-fizzbuzzbongo-0.0.1-jar-with-dependencies.jar"
	dynamoDbTable = "FizzBuzzBongoUserData"
}