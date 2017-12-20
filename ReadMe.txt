Path for code:
Java: src/main/java/impl/
Aws has cluster setup code.
Proxy has VPN code for both client and server.
Radio folder has code for gRPC communication.
Local contains classes to run on client machines.
Remote cotains classes to run on remote machines.
Proto: src/main/java/proto
Generated gRPC classes: src/main/proto

To run on the client machine:
java -cp target/dist-final-1.0-jar-with-dependencies.jar impl.proxy.local.StartClient

To run on proxy machines:
java -cp target/dist-final-1.0-jar-with-dependencies.jar impl.proxy.remote.StartProxy

The jar has all dependencies built in.

Although proxy machines can be cycled on and off, they must be configured to run the above command on startup and
Aws Access Key should be setup to access EC2 machines through the java API:
https://aws.amazon.com/developers/getting-started/java/

Currently this is disabled by commenting out
 proxyInstancesManager.start();
 in the start method of ClientManager.java

So automatic handling of AWS machines is disabled and project can be run if the jar is installed and running on the
remote machines.

