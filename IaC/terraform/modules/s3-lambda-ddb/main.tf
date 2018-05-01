variable "fizzbuzz_lambda_function_name" {}
variable "fizzbuzz_lambda_object_key" {}
variable "dynamoDbTable" {}

output "fizzbuzz_lambda_arn" {
    value = "${aws_lambda_function.fizzbuzz_lambda.arn}"
}

########################
#
# Lambda
#

data "template_file" "fizzbuzz_lambda_assume_role_policy" {
    template = "${file("${path.module}/lambda-assume-role-policy.json")}"
}

data "template_file" "fizzbuzz_lambda_role_policy" {
    template = "${file("${path.module}/lambda-role-policy.json")}"
}

resource "aws_iam_role" "fizzbuzz_lambda_assume_role" {
  name = "${var.fizzbuzz_lambda_function_name}"
  assume_role_policy = "${data.template_file.fizzbuzz_lambda_assume_role_policy.rendered}"
}

resource "aws_lambda_function" "fizzbuzz_lambda" {
  function_name    = "${var.fizzbuzz_lambda_function_name}"
  runtime          = "java8"
  filename         = "../../../target/${var.fizzbuzz_lambda_object_key}"
  handler          = "com.adrianmilne.fizzbuzz.GameSpeechletRequestStreamHandler"
  role             = "${aws_iam_role.fizzbuzz_lambda_assume_role.arn}"
  memory_size      = "512"
  timeout          = "30"
}

resource "aws_iam_policy" "fizzbuzz_lambda_policy" {
  name   = "${var.fizzbuzz_lambda_function_name}-policy"
  path   = "/"
  policy = "${data.template_file.fizzbuzz_lambda_role_policy.rendered}"
}

resource "aws_iam_policy_attachment" "fizzbuzz_lambda_policy_attach" {
  name       = "${var.fizzbuzz_lambda_function_name}-policy-attachment"
  roles      = ["${aws_iam_role.fizzbuzz_lambda_assume_role.name}"]
  policy_arn = "${aws_iam_policy.fizzbuzz_lambda_policy.arn}"
}

# create the Alexa trigger
resource "aws_lambda_permission" "alexa-trigger" {
  statement_id 	= "AllowExecutionFromAlexa"
  action 		= "lambda:InvokeFunction"
  function_name = "${aws_lambda_function.fizzbuzz_lambda.function_name}"
  principal     = "alexa-appkit.amazon.com"
}


########################
#
# DynamoDB
#

resource "aws_dynamodb_table" "fizzbuzz_dynamodb_table" {
  name           = "${var.dynamoDbTable}"
  read_capacity  = 1
  write_capacity = 1
  hash_key       = "UserId"

  attribute {
    name = "UserId"
    type = "S"
  }

  ttl {
    attribute_name = "TimeToExist"
    enabled = false
  }
  
  tags {
    Name = "demo"
  }
}
