# crokinolecentre.com

## Requirements

- [Clojure](https://clojure.org/guides/getting_started)
- [Terraform](https://www.terraform.io)
- [AWS CLI](https://aws.amazon.com/cli/)
- [youtube-dl](https://github.com/ytdl-org/youtube-dl/)

## Usage

### Youtube Posts

Generate posts for all YouTube videos.

```sh
./scripts/youtube.sh
```

### Development

```sh
clojure -A:dev -m cryogen.dev
```

### Build

```sh
clojure -A:build
```

### Deploy

```sh
terraform init scripts/terraform/
terraform plan scripts/terraform/
terraform apply -auto-approve scripts/terraform/
```
