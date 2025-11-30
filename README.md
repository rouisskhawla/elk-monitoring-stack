# Spring Boot ELK Monitoring

This is a Spring Boot application set up with the **ELK stack** (Elasticsearch, Logstash, Kibana) to centralize and visualize logs. The idea is to have your logs structured, searchable, and easy to monitor from a single place.

---

## What’s in this project

- Logs structured with **Logback**
- Environment-specific logging:
  - **Production:** Logs go to Logstash via TCP and then to Elasticsearch
  - **Development:** Logs show up on the console and are saved in a rolling file
- Elasticsearch with SSL and basic authentication
- Kibana dashboards to visualize logs in real time

---

## How the project is organized

```
src/
├─ main/
│  └─ java/
├─ resources/
│  └─ logback.xml  # Logging configuration
docs/
└─ screenshots/    # Screenshots of Kibana dashboards and logs
```

---

## Logging Setup

The `logback.xml` is configured for different environments:

- **Production (`prod`):** Sends logs to Logstash TCP on port 4560, with custom fields:

```xml
<customFields>{"type":"elk-monitoring", "environment":"prod"}</customFields>
```

- **Development (`dev`):** Logs to both console and a rolling file at `./logs/elk-monitoring.log`.

---

## Setting up the ELK stack

### Elasticsearch

1. Install Elasticsearch on the VM.
2. Enable SSL and basic authentication.
3. Store the password in an environment variable:

```bash
export ELASTIC_PASSWORD=<your_password>
```

4. Verify the connection:

```bash
sudo curl --cacert /etc/elasticsearch/certs/http_ca.crt -u elastic:$ELASTIC_PASSWORD https://localhost:9200
```

---

### Logstash

1. Create `/etc/logstash/conf.d/logstash.conf`:

```conf
input {
  tcp {
    port => 4560
    codec => json_lines
  }
}

filter {
  grok {
    match => { "message" => "%{LOGLEVEL:loglevel} %{GREEDYDATA:message}" }
  }
  mutate {
    remove_field => ["@version", "thread_name", "tags", "level_value", "type.keyword", "message.keyword"]
  }
}

output {
  elasticsearch {
      hosts => ["http://localhost:9200"]
      index => "%{type}-%{+YYYY.MM.dd}"
      user => "elastic"
      password => "pass"
      cacert => "/etc/elasticsearch/certs/http_ca.crt"
  }
}
```

2. Make sure Logstash can read the certificate:

```bash
sudo chmod 644 /etc/elasticsearch/certs/http_ca.crt
sudo chmod 644 /etc/elasticsearch/certs
sudo chmod 644 /etc/elasticsearch
```

---

### Kibana

1. Copy the Elasticsearch certificate:

```bash
sudo mkdir /etc/kibana/certs/
sudo cp /etc/elasticsearch/certs/http_ca.crt /etc/kibana/certs/http_ca.crt
sudo chown root:kibana /etc/kibana/certs/http_ca.crt
sudo chmod 644 /etc/kibana/certs/http_ca.crt
```

2. Reset the `kibana_system` password:

```bash
sudo /usr/share/elasticsearch/bin/elasticsearch-reset-password -u kibana_system
```

3. Update `/etc/kibana/kibana.yml`:

```yaml
server.host: "0.0.0.0"
elasticsearch.hosts: ["https://localhost:9200"]
elasticsearch.username: "kibana_system"
elasticsearch.password: "password"
elasticsearch.ssl.certificateAuthorities: [ "/etc/kibana/certs/http_ca.crt" ]
elasticsearch.ssl.verificationMode: full
```

4. Restart Kibana and check the status:

```bash
sudo systemctl restart kibana
sudo systemctl status kibana
```

5. Access Kibana in browser at `http://<host-only-ip>:5601` and log in using the `elastic` user.

6. Create a **data view** with the index pattern `elk-monitoring*`.

---

## Screenshots

Find screenshots of Kibana dashboards and logs in the `/docs/screenshots/` folder.


---

