#!/bin/sh
# vim:sw=2:ts=2:sts=2:et

set -eu

htpasswd -b -c /etc/nginx/.htpasswd ${ADMIN_USERNAME} ${ADMIN_PASSWORD} > /dev/null 2>&1
