import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

public class RakutenAPI {
    public HttpResponse<String> getRestaurantByLocation(){
        HttpResponse<String> response = null;
        try {
            response = Unirest.get("https://us-restaurant-menus.p.rapidapi.com/restaurants/search/geo?page=1&lon=-73.992378&lat=40.68919&distance=1")
                    .header("x-rapidapi-host", "us-restaurant-menus.p.rapidapi.com")
                    .header("x-rapidapi-key", "902ed763c3msh7ed5159aaf15436p1deb2ajsn55718f0bba37")
                    .asString();
        } catch (UnirestException e) {
            e.printStackTrace();
        }

        return response;
    }

    public HttpResponse<String> getRestaurant(int page){
        HttpResponse<String> response = null;
        try {
            response = Unirest.get("https://us-restaurant-menus.p.rapidapi.com/restaurants/search?page=" + page)
                    .header("x-rapidapi-host", "us-restaurant-menus.p.rapidapi.com")
                    .header("x-rapidapi-key", "902ed763c3msh7ed5159aaf15436p1deb2ajsn55718f0bba37")
                    .asString();
        } catch (UnirestException e) {
            e.printStackTrace();
        }

        return response;
    }

    public HttpResponse<String> getMenuItem(String restaurant_id, int page){
        HttpResponse<String> response = null;
        try {
            response = Unirest.get("https://us-restaurant-menus.p.rapidapi.com/restaurant/+" + restaurant_id +"/menuitems?page=" + page)
                    .header("x-rapidapi-host", "us-restaurant-menus.p.rapidapi.com")
                    .header("x-rapidapi-key", "902ed763c3msh7ed5159aaf15436p1deb2ajsn55718f0bba37")
                    .asString();
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        return response;
    }
}