package radio;

import io.grpc.Server;

public class RadioMaster {

    private Server server;

    private class Terminator extends radio.TerminatorGrpc {

    }

}
