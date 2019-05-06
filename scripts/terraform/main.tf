provider "aws" {
  version = "~> 2.6"
  region = "ca-central-1"
}

provider "null" {
  version = "~> 2.1"
}

terraform {
  backend "s3" {
    bucket = "tfstate-crokinolecentre.com"
    key    = "aws/ca-central-1/terraform.tfstate"
    region = "ca-central-1"
  }
}

variable "domain_name" {
  default     = "crokinolecentre.com"
  type        = "string"
}

data "aws_iam_policy_document" "main_bucket_read" {
  statement = {
    actions   = ["s3:GetObject"]
    resources = ["arn:aws:s3:::${var.domain_name}/*"]

    principals {
      type        = "AWS"
      identifiers = ["*"]
    }
  }
}

resource "aws_s3_bucket" "main" {
  bucket        = "${var.domain_name}"
  acl           = "public-read"
  policy        = "${data.aws_iam_policy_document.main_bucket_read.json}"
  force_destroy = true

  website {
    index_document = "index.html"
    error_document = "404.html"
  }
}

data "aws_iam_policy_document" "www_main_bucket_read" {
  statement = {
    actions   = ["s3:GetObject"]
    resources = ["arn:aws:s3:::www.${var.domain_name}/*"]

    principals {
      type        = "AWS"
      identifiers = ["*"]
    }
  }
}

resource "aws_s3_bucket" "main_www" {
  bucket        = "www.${var.domain_name}"
  acl           = "public-read"
  policy        = "${data.aws_iam_policy_document.www_main_bucket_read.json}"
  force_destroy = true

  website {
    redirect_all_requests_to = "${var.domain_name}"
  }
}

resource "null_resource" "s3_sync" {
  depends_on = ["aws_s3_bucket.main"]

  provisioner "local-exec" {
    working_dir = "${path.module}/../../resources/public"
    command     = "aws s3 sync . s3://${aws_s3_bucket.main.bucket} --acl bucket-owner-full-control --acl public-read"
  }
}
