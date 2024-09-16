build-app:
	docker build --build-arg SECRET_KEY=yourSecretKey --build-arg DB_URL=yourDbUrl --build-arg DB_USERNAME=yourDbUsername --build-arg DB_PASSWORD=yourDbPassword -f Dockerfile -t taitra-server .
docker_test_build:
	docker run -it -v $(shell pwd):/TAITRA openjdk:11 /bin/bash
docker_build_image_run:
	docker run --rm -it  taitar-server:latest /bin/bash