# Deployment Guide

This guide covers deploying the Contact Management System to production environments.

## Table of Contents
1. [Pre-Deployment Checklist](#pre-deployment-checklist)
2. [Database Deployment](#database-deployment)
3. [Backend Deployment](#backend-deployment)
4. [Frontend Deployment](#frontend-deployment)
5. [Configuration Management](#configuration-management)
6. [Monitoring & Logging](#monitoring--logging)
7. [Troubleshooting](#troubleshooting)
8. [Rollback Procedures](#rollback-procedures)

---

## Pre-Deployment Checklist

### Security
- [ ] Change all default passwords
- [ ] Generate new JWT secret (min 32 characters)
- [ ] Enable HTTPS/TLS certificates
- [ ] Configure firewall rules
- [ ] Enable SQL Server encryption
- [ ] Review Spring Security configuration
- [ ] Disable debug endpoints in production
- [ ] Configure CORS for production domains only
- [ ] Enable rate limiting on API endpoints
- [ ] Set up WAF (Web Application Firewall)
- [ ] Enable SQL Server audit logging
- [ ] Rotate API keys and secrets
- [ ] Enable HSTS headers

### Performance
- [ ] Configure connection pooling
- [ ] Enable query caching
- [ ] Optimize database indexes
- [ ] Test with production-like load
- [ ] Configure CDN for static assets
- [ ] Enable compression (gzip)
- [ ] Set up database backups
- [ ] Configure log rotation
- [ ] Test failover procedures

### Infrastructure
- [ ] Provision servers/containers
- [ ] Configure load balancer
- [ ] Set up database backup storage
- [ ] Configure monitoring and alerts
- [ ] Set up centralized logging
- [ ] Configure DNS records
- [ ] Set up CI/CD pipeline
- [ ] Test disaster recovery plan

### Application
- [ ] Update version numbers
- [ ] Build and test Docker images
- [ ] Run full test suite
- [ ] Verify code quality (SonarQube)
- [ ] Update documentation
- [ ] Create deployment plan
- [ ] Schedule maintenance window
- [ ] Notify stakeholders

---

## Database Deployment

### SQL Server on Azure
```powershell
# Create Azure SQL Database
az sql server create `
  --name contact-mgmt-server `
  --resource-group contact-app-rg `
  --location eastus `
  --admin-user sqladmin `
  --admin-password YourSecurePassword123!

# Create database
az sql db create `
  --resource-group contact-app-rg `
  --server contact-mgmt-server `
  --name ContactManagementDB `
  --edition Standard `
  --capacity 10
```

### SQL Server on AWS (RDS)
```bash
# Using AWS CLI
aws rds create-db-instance \
  --db-instance-identifier contact-mgmt-db \
  --db-instance-class db.t3.micro \
  --engine sqlserver-ex \
  --master-username admin \
  --master-user-password YourSecurePassword123! \
  --allocated-storage 100
```

### Run Database Setup
```bash
# Using sqlcmd
sqlcmd -S server.database.windows.net -U admin@server -P YourPassword \
  -d ContactManagementDB -i database/setup.sql

# Using Docker
docker exec contact-mgmt-db sqlcmd -U sa -P YourPassword \
  -d ContactManagementDB -i /setup.sql
```

### Database Backup
```sql
-- SQL Server backup
BACKUP DATABASE [ContactManagementDB] 
TO DISK = 'D:\backups\ContactManagementDB_full.bak'
WITH COMPRESSION, STATS = 10;

-- Scheduled backup (weekly)
-- Set up SQL Server Agent job or use cloud provider backup service
```

---

## Backend Deployment

### Option 1: Docker Container on Cloud

#### Azure Container Instances
```bash
# Build and push image
docker build -f Dockerfile.backend -t contact-mgmt-api:1.0.0 .
docker tag contact-mgmt-api:1.0.0 myregistry.azurecr.io/contact-mgmt-api:1.0.0
docker push myregistry.azurecr.io/contact-mgmt-api:1.0.0

# Deploy
az container create \
  --resource-group contact-app-rg \
  --name contact-api \
  --image myregistry.azurecr.io/contact-mgmt-api:1.0.0 \
  --ports 8080 \
  --environment-variables \
    SPRING_DATASOURCE_URL=jdbc:sqlserver://server:1433 \
    SPRING_DATASOURCE_USERNAME=admin \
    APP_JWT_SECRET=your_secret_key
```

#### AWS ECS/EKS
```bash
# Push to ECR
aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin 123456789.dkr.ecr.us-east-1.amazonaws.com
docker tag contact-mgmt-api:1.0.0 123456789.dkr.ecr.us-east-1.amazonaws.com/contact-mgmt-api:1.0.0
docker push 123456789.dkr.ecr.us-east-1.amazonaws.com/contact-mgmt-api:1.0.0

# Deploy to ECS (create task definition and service)
```

### Option 2: Kubernetes Deployment

#### Create ConfigMap for Configuration
```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: backend-config
  namespace: contact-app
data:
  application.properties: |
    spring.datasource.url=jdbc:sqlserver://mssql-service:1433;databaseName=ContactManagementDB
    spring.datasource.username=admin
    app.jwt.expiration=86400000
```

#### Create Secret for Sensitive Data
```yaml
apiVersion: v1
kind: Secret
metadata:
  name: backend-secret
  namespace: contact-app
type: Opaque
stringData:
  spring.datasource.password: YourSecurePassword123!
  app.jwt.secret: your_very_long_secret_key_min_32_chars
```

#### Deploy Backend
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: contact-api
  namespace: contact-app
spec:
  replicas: 3
  selector:
    matchLabels:
      app: contact-api
  template:
    metadata:
      labels:
        app: contact-api
    spec:
      containers:
      - name: api
        image: myregistry.azurecr.io/contact-mgmt-api:1.0.0
        ports:
        - containerPort: 8080
        env:
        - name: SPRING_DATASOURCE_PASSWORD
          valueFrom:
            secretKeyRef:
              name: backend-secret
              key: spring.datasource.password
        - name: APP_JWT_SECRET
          valueFrom:
            secretKeyRef:
              name: backend-secret
              key: app.jwt.secret
        livenessProbe:
          httpGet:
            path: /api/contacts
            port: 8080
          initialDelaySeconds: 30
          periodSeconds: 10
        readinessProbe:
          httpGet:
            path: /api/contacts
            port: 8080
          initialDelaySeconds: 5
          periodSeconds: 5
        resources:
          requests:
            memory: "256Mi"
            cpu: "250m"
          limits:
            memory: "512Mi"
            cpu: "500m"
```

#### Service for Backend
```yaml
apiVersion: v1
kind: Service
metadata:
  name: contact-api-service
  namespace: contact-app
spec:
  type: ClusterIP
  ports:
  - port: 8080
    targetPort: 8080
  selector:
    app: contact-api
```

---

## Frontend Deployment

### Option 1: CDN with Static Hosting

#### Azure Static Web Apps
```bash
# Build frontend
cd frontend && npm run build

# Deploy to Azure
az staticwebapp create \
  --name contact-app-web \
  --source ./frontend/dist \
  --resource-group contact-app-rg \
  --location eastus
```

#### AWS S3 + CloudFront
```bash
# Build frontend
cd frontend && npm run build

# Upload to S3
aws s3 sync frontend/dist/ s3://contact-app-bucket/

# Create CloudFront distribution
aws cloudfront create-distribution \
  --origin-domain-name contact-app-bucket.s3.amazonaws.com \
  --default-root-object index.html
```

### Option 2: Container Deployment

#### Docker on App Service
```bash
# Build frontend image
docker build -f Dockerfile.frontend -t contact-mgmt-web:1.0.0 -C frontend .

# Push to registry
docker push myregistry.azurecr.io/contact-mgmt-web:1.0.0

# Deploy to Azure App Service
az appservice plan create \
  --name contact-app-plan \
  --resource-group contact-app-rg \
  --sku B1 \
  --is-linux

az webapp create \
  --resource-group contact-app-rg \
  --plan contact-app-plan \
  --name contact-app-web \
  --deployment-container-image-name myregistry.azurecr.io/contact-mgmt-web:1.0.0
```

### Option 3: Kubernetes Deployment
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: contact-web
  namespace: contact-app
spec:
  replicas: 3
  selector:
    matchLabels:
      app: contact-web
  template:
    metadata:
      labels:
        app: contact-web
    spec:
      containers:
      - name: web
        image: myregistry.azurecr.io/contact-mgmt-web:1.0.0
        ports:
        - containerPort: 80
        env:
        - name: VITE_API_BASE_URL
          value: "https://api.yourdomain.com"
        resources:
          requests:
            memory: "128Mi"
            cpu: "100m"
          limits:
            memory: "256Mi"
            cpu: "200m"
```

---

## Configuration Management

### Environment Variables

#### Backend (application.properties)
```properties
# Database
spring.datasource.url=jdbc:sqlserver://prod-server:1433;databaseName=ContactManagementDB;encrypt=true;trustServerCertificate=false
spring.datasource.username=admin
spring.datasource.password=${DB_PASSWORD}
spring.jpa.hibernate.ddl-auto=validate

# JWT
app.jwt.secret=${JWT_SECRET}
app.jwt.expiration=86400000

# Logging
logging.level.root=WARN
logging.level.com.contactmanager=INFO
logging.file.name=/var/log/contactmanager/app.log

# SSL
server.ssl.key-store=${SSL_KEYSTORE_PATH}
server.ssl.key-store-password=${SSL_KEYSTORE_PASSWORD}
server.ssl.key-store-type=PKCS12

# CORS
app.cors.allowed-origins=https://yourdomain.com,https://www.yourdomain.com

# Profiles
spring.profiles.active=prod
```

#### Frontend (.env.production)
```
VITE_API_BASE_URL=https://api.yourdomain.com
VITE_APP_VERSION=1.0.0
```

### Secrets Management

#### Azure Key Vault
```bash
# Store secrets
az keyvault secret set \
  --vault-name contact-app-kv \
  --name db-password \
  --value YourSecurePassword123!

az keyvault secret set \
  --vault-name contact-app-kv \
  --name jwt-secret \
  --value your_very_long_secret_key

# Reference in deployment
az appservice config appsettings set \
  --resource-group contact-app-rg \
  --name contact-app-api \
  --settings @secrets.json
```

#### AWS Secrets Manager
```bash
aws secretsmanager create-secret \
  --name contact-app/db-password \
  --secret-string YourSecurePassword123!

aws secretsmanager create-secret \
  --name contact-app/jwt-secret \
  --secret-string your_very_long_secret_key
```

---

## Monitoring & Logging

### Application Performance Monitoring

#### Application Insights (Azure)
```xml
<!-- Add to pom.xml -->
<dependency>
  <groupId>com.microsoft.azure</groupId>
  <artifactId>applicationinsights-spring-boot-starter</artifactId>
  <version>2.6.1</version>
</dependency>
```

```properties
# application.properties
appinsights.instrumentationkey=${INSTRUMENTATION_KEY}
appinsights.web.enable-W3c=true
```

#### DataDog
```yaml
# docker-compose.yml addition
datadog:
  image: gcr.io/datadoghq/agent:latest
  environment:
    DD_API_KEY: ${DATADOG_API_KEY}
    DD_SITE: datadoghq.com
  volumes:
    - /var/run/docker.sock:/var/run/docker.sock
```

### Centralized Logging

#### ELK Stack (Elasticsearch, Logstash, Kibana)
```xml
<!-- Add to pom.xml -->
<dependency>
  <groupId>net.logstash.logback</groupId>
  <artifactId>logstash-logback-encoder</artifactId>
  <version>7.4</version>
</dependency>
```

#### Azure Log Analytics
```bash
# Create workspace
az monitor log-analytics workspace create \
  --resource-group contact-app-rg \
  --workspace-name contact-logs
```

### Alerting

```yaml
# Example alert rules
alerts:
  - name: HighErrorRate
    metric: http_requests_total
    condition: error_rate > 5%
    action: send_email

  - name: DatabaseDown
    metric: database_connection_pool
    condition: available_connections == 0
    action: send_sms_and_email

  - name: HighResponseTime
    metric: http_request_duration
    condition: p99_latency > 1000ms
    action: send_email
```

---

## Troubleshooting

### Backend Issues

#### Application won't start
```bash
# Check logs
tail -f /var/log/contactmanagement/app.log

# Check database connection
telnet prod-server 1433

# Verify environment variables
docker inspect container-id | grep -E "DB_|JWT_|SPRING_"
```

#### High memory usage
```bash
# Check JVM settings
ps aux | grep java | grep Xmx

# Adjust in Docker
# Add to Dockerfile: ENV JAVA_OPTS="-Xms256m -Xmx512m"
```

#### Database connection pool exhausted
```properties
# Increase pool size in application.properties
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5
```

### Frontend Issues

#### Slow performance
1. Check bundle size: `npm run build && npm run analyze`
2. Enable gzip: Verify nginx.conf has gzip enabled
3. Check CDN cache headers
4. Optimize images

#### CORS errors
1. Verify backend CORS configuration
2. Check allowed origins in SecurityConfig
3. Ensure requests use correct domain

### Database Issues

#### Slow queries
```sql
-- Find slow queries
SELECT * FROM sys.dm_exec_query_stats
ORDER BY total_elapsed_time DESC
LIMIT 10;

-- Check indexes
EXEC sp_helpindex 'contacts';
```

#### Connection timeouts
```properties
spring.datasource.hikari.connection-timeout=20000
spring.datasource.hikari.idle-timeout=300000
spring.datasource.hikari.max-lifetime=1200000
```

---

## Rollback Procedures

### Database Rollback
```bash
# Restore from backup
RESTORE DATABASE [ContactManagementDB] 
FROM DISK = 'D:\backups\ContactManagementDB_full.bak'
WITH REPLACE;
```

### Application Rollback (Kubernetes)
```bash
# Rollback to previous deployment
kubectl rollout undo deployment/contact-api -n contact-app

# Rollback to specific revision
kubectl rollout history deployment/contact-api -n contact-app
kubectl rollout undo deployment/contact-api --to-revision=2 -n contact-app
```

### Docker Rollback
```bash
# Stop current container
docker stop contact-api
docker rm contact-api

# Run previous version
docker run -d --name contact-api \
  myregistry.azurecr.io/contact-mgmt-api:0.9.0
```

---

## Maintenance

### Regular Tasks

- **Daily:** Monitor logs and alerts
- **Weekly:** Database integrity checks, backup verification
- **Monthly:** Security updates, dependency updates
- **Quarterly:** Performance review, capacity planning

### Update Process

1. Test updates in staging environment
2. Schedule maintenance window
3. Create database backup
4. Deploy new version
5. Smoke test key functionality
6. Monitor metrics and logs
7. Have rollback plan ready

---

## Support & Escalation

### On-Call Procedures
- P1 (Critical): < 15 min response
- P2 (High): < 1 hour response
- P3 (Medium): < 4 hours response
- P4 (Low): Next business day

### Escalation Path
1. Application Support Team
2. DevOps Team
3. DBA Team
4. Infrastructure Team
5. Vendor Support (if applicable)

---

**Last Updated:** August 2026
**Version:** 1.0.0
