variable "aws_region" {
  description = "Região AWS para deploy"
  type        = string
  default     = "us-east-1"
}

variable "environment" {
  description = "Ambiente de deployment"
  type        = string
  default     = "production"
}

# VPC Variables
variable "vpc_cidr" {
  description = "CIDR block para VPC APOFlow"
  type        = string
  default     = "10.0.0.0/16"
}

variable "public_subnet_cidr" {
  description = "CIDR block para subnet pública (EC2)"
  type        = string
  default     = "10.0.1.0/24"
}

# Subnets privadas não são usadas (sem RDS), mas mantidas para compatibilidade
variable "private_subnet_cidr" {
  description = "CIDR block para subnet privada (RDS)"
  type        = string
  default     = "10.0.2.0/24"
}

variable "private_subnet_2_cidr" {
  description = "CIDR block para segunda subnet privada (RDS)"
  type        = string
  default     = "10.0.3.0/24"
}

# EC2 Variables
variable "instance_type" {
  description = "Tipo de instância EC2"
  type        = string
  default     = "t3.small"
}

variable "ec2_key_pair_name" {
  description = "Nome da key pair para SSH"
  type        = string
  default     = "apoflow-key"
}

variable "mailersend_token" {
  description = "Token da API MailerSend para envio de e-mails"
  type        = string
  sensitive   = true
}

variable "mailersend_from" {
  description = "Endereço remetente do MailerSend (domínio verificado)"
  type        = string
}

variable "jwt_secret" {
  description = "Chave secreta para assinar tokens JWT (mínimo 32 caracteres)"
  type        = string
  sensitive   = true
}
