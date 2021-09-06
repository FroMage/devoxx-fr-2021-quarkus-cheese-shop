# cheese-shop project

Demo used for 2021 Summit Talk 

# STEPS

- fix docker/kafka for DEV Services
 - sudo vim /etc/sysctl.conf # add: fs.aio-max-nr = 1048576
 - sudo sysctl -p
fs.aio-max-nr = 1048576
 - cat /proc/sys/fs/aio-max-nr
1048576
- Install jbang/quarkus
 - curl -Ls https://sh.jbang.dev | bash -s - app install --fresh --force quarkus@quarkusio

 