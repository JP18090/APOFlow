# MongoDB roda em Docker na própria EC2 (mesmo setup do docker-compose local).
# Não é necessário Amazon DocumentDB para este protótipo.

# CloudWatch Log Group para RDS
resource "aws_cloudwatch_log_group" "apoflow_rds_logs" {
  name              = "/aws/rds/apoflow-mongodb"
  retention_in_days = 7

  tags = {
    Name = "apoflow-rds-logs"
  }
}

# Secrets Manager para armazenar credenciais do MongoDB
resource "aws_secretsmanager_secret" "apoflow_mongodb_credentials" {
  name = "apoflow/mongodb/credentials"
  
  tags = {
    Name = "apoflow-mongodb-credentials"
  }
}

resource "aws_secretsmanager_secret_version" "apoflow_mongodb_credentials" {
  secret_id = aws_secretsmanager_secret.apoflow_mongodb_credentials.id
  secret_string = jsonencode({
    username = var.mongodb_username
    password = var.mongodb_password
    engine   = "docdb"
    host     = aws_db_instance.apoflow_mongodb.address
    port     = aws_db_instance.apoflow_mongodb.port
    dbname   = "apoflow"
    connection_string = "mongodb://${var.mongodb_username}:${var.mongodb_password}@${aws_db_instance.apoflow_mongodb.address}:${aws_db_instance.apoflow_mongodb.port}/apoflow?ssl=true&retryWrites=false"
  })
}

# RDS DB Cluster Parameter Group (se usar Cluster mode)
resource "aws_db_cluster_parameter_group" "apoflow_cluster_params" {
  family      = "docdb4.0"
  name        = "apoflow-cluster-params"
  description = "Cluster parameter group para APOFlow MongoDB"

  parameter {
    name  = "profiler"
    value = "1"
  }

  tags = {
    Name = "apoflow-cluster-params"
  }
}

# DB Parameter Group
resource "aws_db_parameter_group" "apoflow_db_params" {
  family      = "docdb4.0"
  name        = "apoflow-db-params"
  description = "DB parameter group para APOFlow MongoDB"

  parameter {
    name  = "profiler"
    value = "1"
  }

  tags = {
    Name = "apoflow-db-params"
  }
}
