package api;

import java.net.URI;
import java.util.List;

public interface PeersHandler {
    List<URI> getLivePeers();
}
