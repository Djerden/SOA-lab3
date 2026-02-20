consul {
  address = "consul:8500"
  
  retry {
    enabled = true
    attempts = 12
    backoff = "250ms"
    max_backoff = "1m"
  }
}

template {
  source = "/etc/haproxy/templates/haproxy.cfg.ctmpl"
  destination = "/usr/local/etc/haproxy/haproxy.cfg"
  
  # Reload HAProxy when config changes
  command = "haproxy -c -f /usr/local/etc/haproxy/haproxy.cfg && kill -SIGUSR2 $(cat /var/run/haproxy/haproxy.pid) 2>/dev/null || true"
  command_timeout = "60s"
  
  # Error handling
  error_on_missing_key = false
  
  # Wait for services to be available
  wait {
    min = "2s"
    max = "10s"
  }
}

# Log level
log_level = "info"
