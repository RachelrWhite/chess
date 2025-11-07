package Facade;

import com.google.gson.Gson;
//import Exception.ResponseException;

import java.net.*;
import java.net.http.*;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;


public class ServerFacade {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;
    }

    //this is where they called the public Pet addPet(pet pet) throws ResponceException
    //public listPets()
    //need to make these for all the different endpoints


    private HttpRequest buildRequest(String method, String path, Object body) {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + path))
                .method(method, makeRequestBody(body));
        if (body != null) {
            request.setHeader("Content-Type", "application/json");
        }
        return request.build();
    }

    private BodyPublisher makeRequestBody(Object request) {
        if (request != null) {
            return BodyPublishers.ofString(new Gson().toJson(request));
        } else {
            return BodyPublishers.noBody();
        }
    }

    private HttpResponse<String> sendRequest(HttpRequest request) {
        try {
            return client.send(request, BodyHandlers.ofString());
        } catch (Exception ex) {
            System.out.print("we freaked ServerFacade");
//            throw new ResponseException(ResponseException.Code.ServerError, ex.getMessage());
            return null;
        }
    }

    private <T> T handleResponse(HttpResponse<String> response, Class<T> responseClass)  {
        var status = response.statusCode();
        if (!isSuccessful(status)) {
            var body = response.body();
            if (body != null) {
//                throw ResponseException.fromJson(body);
                System.out.print("we freaked ServerFacade2");
            }

            System.out.print("we freaked ServerFacade3");
            //throw new ResponseException(ResponseException.fromHttpStatusCode(status), "other failure: " + status);
        }

        if (responseClass != null) {
            return new Gson().fromJson(response.body(), responseClass);
        }

        return null;
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }


}
