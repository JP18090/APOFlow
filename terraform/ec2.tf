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

# AWS Academy (voclabs) não permite criar IAM roles.
# Usa o LabInstanceProfile pré-existente no ambiente.
data "aws_iam_instance_profile" "lab_profile" {
  name = "LabInstanceProfile"
}

# EC2 Instance
resource "aws_instance" "apoflow_server" {
  ami                    = data.aws_ami.ubuntu.id
  instance_type          = var.instance_type
  subnet_id              = aws_subnet.apoflow_public_subnet.id
  vpc_security_group_ids = [aws_security_group.apoflow_ec2_sg.id]
  iam_instance_profile   = data.aws_iam_instance_profile.lab_profile.name
  key_name               = var.ec2_key_pair_name

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
