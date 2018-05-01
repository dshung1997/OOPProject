package Model;

import java.util.Date;
import java.util.List;

public class MovieDisc extends Product {
    private List<String> listActors;
    private String director;

    private Enum<Language> language;
    private Enum<Language> subtitle;
    private Enum<MovieGenre> genre;
    private int length;
    private float imdbPoint;
    private Date publicDate;

    public MovieDisc(String _productID, String _name, Enum<Category> _category, Enum<Status> _status, int _quantity, double _buyingPrice, double _sellingPrice, Enum<Nation> _nation, String _imageUrl, int _discount, List<String> _listActors, String _director, Enum<Language> _language,  Enum<Language> _subtitle, Enum<MovieGenre> _genre, int _length, float _imdbPoint, Date _publicDate)
    {
        super(_productID, _name, _category, _status, _quantity, _buyingPrice, _sellingPrice, _nation, _imageUrl, _discount);

        this.setListActors(_listActors);
        this.setDirector(_director);

        this.setLanguage(_language);
        this.setSubtitle(_subtitle);
        this.setGenre(_genre);
        this.setLength(_length);
        this.setImdbPoint(_imdbPoint);
        this.setPublicDate(_publicDate);

    }

    public List<String> getListActors() {
        return listActors;
    }

    public void setListActors(List<String> listActors) {
        this.listActors = listActors;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public Enum<Language> getLanguage() {
        return language;
    }

    public void setLanguage(Enum<Language> language) {
        this.language = language;
    }

    public Enum<Language> getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(Enum<Language> subtitle) {
        this.subtitle = subtitle;
    }

    public Enum<MovieGenre> getGenre() {
        return genre;
    }

    public void setGenre(Enum<MovieGenre> genre) {
        this.genre = genre;
    }

    public float getImdbPoint() {
        return imdbPoint;
    }

    public void setImdbPoint(float imdbPoint) {
        this.imdbPoint = imdbPoint;
    }

    public Date getPublicDate() {
        return publicDate;
    }

    public void setPublicDate(Date publicDate) {
        this.publicDate = publicDate;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }
}
