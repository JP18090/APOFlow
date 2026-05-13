# Data source para obter a AMI mais recente do Ubuntu
data "aws_ami" "ubuntu" {
  most_recent = true
  owners      = ["099720109477"] # Canonical

  filter {
    name   = "name"
    values = ["ubuntu/images/hvm-ssd/ubuntu-jammy-22.04-amd64-server-*"]
  }

  filter {
    name   = "root-device-type"
    values = ["ebs"]
  }

  filter {
    name   = "virtualization-type"
    values = ["hvm"]
  }
}

# IAM Role para EC2
resource "aws_iam_role" "apoflow_ec2_role" {
  name = "apoflow-ec2-role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action = "sts:AssumeRole"
        Effect = "Allow"
        Principal = {
          Service = "ec2.amazonaws.com"
        }
      }
    ]
  })
}

# IAM Policy para CloudWatch Logs
resource "aws_iam_role_policy" "apoflow_ec2_cloudwatch_policy" {
  name = "apoflow-ec2-cloudwatch-policy"
  role = aws_iam_role.apoflow_ec2_role.id

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect = "Allow"
        Action = [
          "logs:CreateLogGroup",
          "logs:CreateLogStream",
          "logs:PutLogEvents",
          "logs:DescribeLogStreams"
        ]
        Resource = "arn:aws:logs:*:*:*"
      }
    ]
  })
}

# IAM Instance Profile
resource "aws_iam_instance_profile" "apoflow_ec2_profile" {
  name = "apoflow-ec2-profile"
  role = aws_iam_role.apoflow_ec2_role.name
}

# EC2 Instance
resource "aws_instance" "apoflow_server" {
  ami                    = data.aws_ami.ubuntu.id
  instance_type          = var.instance_type
  subnet_id              = aws_subnet.apoflow_public_subnet.id
  vpc_security_group_ids = [aws_security_group.apoflow_ec2_sg.id]
  iam_instance_profile   = aws_iam_instance_profile.apoflow_ec2_profile.name

  user_data = templatefile("${path.module}/user_data.sh", {
    mailersend_token = var.mailersend_token
    mailersend_from  = var.mailersend_from
    jwt_secret       = var.jwt_secret
  })

  root_block_device {
    volume_type           = "gp3"
    volume_size           = 20
    delete_on_termination = true
    encrypted             = true

    tags = {
      Name = "apoflow-root-volume"
    }
  }

  monitoring = true

  tags = {
    Name = "apoflow-server"
  }

  depends_on = [aws_internet_gateway.apoflow_igw]
}

# Elastic IP para EC2
resource "aws_eip" "apoflow_eip" {
  instance = aws_instance.apoflow_server.id
  domain   = "vpc"

  tags = {
    Name = "apoflow-eip"
  }

  depends_on = [aws_internet_gateway.apoflow_igw]
}
