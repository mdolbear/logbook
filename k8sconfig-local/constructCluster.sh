
#logbook
kubectl apply -f sa/logbook-service-account.yml
kubectl create -f logbook/logbook-config-map.yml
kubectl create -f logbook/logbook-deployment.yml
kubectl create -f logbook/logbook-service.yml

#Ingress nginx
kubectl create -f ingress/ingress-controller.yml
echo "Will wait 60 seconds for ingress-controller to come up."
echo "If you see an error message, you will need"
echo " to run kubectl create -f ingress/ingress.yml by hand."
echo "  "
sleep 60
kubectl create -f ingress/multipod-ingress.yml


