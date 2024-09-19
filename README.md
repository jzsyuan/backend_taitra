# Docker backend_taitra

## Docker Build
```
    docker build -f Dockerfile -t backend_taitra .
```

## Docker commend  
```
docker run --rm -it \
	-e SECRET_KEY="********" \
	-e DB_URL="jdbc:postgresql://********:5432/ *****" \
	-e DB_USERNAME="*****" \
	-e DB_PASSWORD="******" \
	-p 9091:9090 backend_taitra:latest
```

## Test API Heartbeat
```bash
curl --request GET \
        --url http://localhost:9091/api/v1/heartbeat \
        --header 'User-Agent: insomnia/8.4.5'
```