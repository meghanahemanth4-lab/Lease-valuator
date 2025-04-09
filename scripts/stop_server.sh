#!/bin/bash
isExistApp=$(systemctl is-active LvaService.service)  # Check if the service is active
if [[ $isExistApp == "active" ]]; then  # If the service is running
    systemctl stop LvaService.service  # Stop the service
    echo "LvaService.service has been stopped."
else
    echo "LvaService.service is not running or does not exist."
fi