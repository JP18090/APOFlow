output "vpc_id" {
  description = "ID da VPC APOFlow"
  value       = aws_vpc.apoflow_vpc.id
}

output "vpc_cidr" {
  description = "CIDR da VPC APOFlow"
  value       = aws_vpc.apoflow_vpc.cidr_block
}

output "public_subnet_id" {
  description = "ID da subnet pública (EC2)"
  value       = aws_subnet.apoflow_public_subnet.id
}

output "ec2_instance_id" {
  description = "ID da instância EC2 APOFlow"
  value       = aws_instance.apoflow_server.id
}

output "ec2_public_ip" {
  description = "IP público da EC2 APOFlow"
  value       = aws_eip.apoflow_eip.public_ip
}

output "ec2_private_ip" {
  description = "IP privado da EC2 APOFlow"
  value       = aws_instance.apoflow_server.private_ip
}

output "ec2_security_group_id" {
  description = "ID do security group da EC2"
  value       = aws_security_group.apoflow_ec2_sg.id
}

output "app_url" {
  description = "URL pública da aplicação APOFlow"
  value       = "http://${aws_eip.apoflow_eip.public_ip}:8080"
}

output "mongodb_resource_id" {
  description = "Resource ID do MongoDB"
  value       = aws_db_instance.apoflow_mongodb.resource_id
}

output "mongodb_arn" {
  description = "ARN do RDS MongoDB"
  value       = aws_db_instance.apoflow_mongodb.arn
}

output "rds_security_group_id" {
  description = "ID do security group do RDS"
  value       = aws_security_group.apoflow_rds_sg.id
}

output "secrets_manager_secret_arn" {
  description = "ARN do secret do MongoDB no AWS Secrets Manager"
  value       = aws_secretsmanager_secret.apoflow_mongodb_credentials.arn
}

output "secrets_manager_secret_name" {
  description = "Nome do secret do MongoDB no AWS Secrets Manager"
  value       = aws_secretsmanager_secret.apoflow_mongodb_credentials.name
}

output "mongodb_connection_string" {
  description = "Connection string do MongoDB (armazenada no Secrets Manager)"
  value       = "Ver no AWS Secrets Manager: ${aws_secretsmanager_secret.apoflow_mongodb_credentials.name}"
}

output "ec2_ssh_command" {
  description = "Comando para conectar via SSH na EC2"
  value       = "ssh -i /caminho/para/key.pem ec2-user@${aws_eip.apoflow_eip.public_ip}"
}

output "terraform_outputs_summary" {
  description = "Resumo dos outputs principais"
  value = {
    vpc_id                        = aws_vpc.apoflow_vpc.id
    ec2_public_ip                 = aws_eip.apoflow_eip.public_ip
    ec2_instance_id               = aws_instance.apoflow_server.id
    mongodb_endpoint              = aws_db_instance.apoflow_mongodb.endpoint
    mongodb_port                  = aws_db_instance.apoflow_mongodb.port
    mongodb_database              = "apoflow"
    mongodb_username              = var.mongodb_username
    secrets_manager_secret_name   = aws_secretsmanager_secret.apoflow_mongodb_credentials.name
  }
}
