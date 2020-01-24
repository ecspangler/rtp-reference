# --- MySQL Installation
oc project rtp-reference
oc new-app \
    -e MYSQL_USER=dbuser \
    -e MYSQL_PASSWORD=dbpass \
    -e MYSQL_DATABASE=rtpdb \
    --name=mysql-56-rhel7 \
    registry.access.redhat.com/rhscl/mysql-56-rhel7

until [ "$(oc get pods --selector app=mysql-56-rhel7 -o jsonpath="{.items[0].status.containerStatuses[?(@.name == \"mysql-56-rhel7\")].ready}" 2> /dev/null)" = "true" ]; do sleep 3; printf "Waiting until container is ready...\n"; done

oc port-forward $(oc get pods --selector app=mysql-56-rhel7 -o jsonpath="{.items[0].metadata.name}") 3306 &> /dev/null &
cpid=$!
trap "kill $cpid" EXIT
until mysql --host localhost -P 3306 --protocol tcp -u dbuser -D rtpdb -pdbpass --execute exit &> /dev/null; do sleep 3; printf "Waiting until MySQL comes up...\n"; done

printf "Loading create_debtor_credit_payment.sql\n"
mysql -w --host localhost -P 3306 --protocol tcp -u dbuser -D rtpdb -pdbpass < ./rtp-debtor-transaction-repository-mysql/src/main/resources/database-scripts/create_debtor_credit_payment.sql
printf "Loading create_debtor_debit_payment.sql\n"
mysql --host localhost -P 3306 --protocol tcp -u dbuser -D rtpdb -pdbpass < ./rtp-debtor-transaction-repository-mysql/src/main/resources/database-scripts/create_debtor_debit_payment.sql
printf "Loading create_creditor_debit_payment.sql\n"
mysql --host localhost -P 3306 --protocol tcp -u dbuser -D rtpdb -pdbpass < ./rtp-creditor-transaction-repository-mysql/src/main/resources/database-scripts/create_creditor_credit_payment.sql
kill $cpid
trap - EXIT
