package model.product;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;

public class MovieDisc extends Product {
    private ArrayList<String> listActors;
    private String director;

    private Enum<Language> language;
    private Enum<Language> subtitle;
    private Enum<MovieGenre> genre;
    private int length;
    private float imdbPoint;
    private LocalDate publicDate;

    public MovieDisc()
    {
        super();
        this.setCategory(Category.MOVIE_DISC);
        this.setListActors(new ArrayList<String>(Arrays.asList("", "", "", "", "")));
        this.setDirector("");

        this.setLanguage(Language.ENGLISH);
        this.setSubtitle(Language.VIETNAMESE);
        this.setGenre(MovieGenre.Action);
        this.setLength(0);
        this.setImdbPoint(0.0f);
        this.setPublicDate(LocalDateTime.now().toLocalDate());
    }

    public MovieDisc(Product _product)
    {
        super(_product.getProductID(), _product.getName(), _product.getCategory(), _product.getStatus(), _product.getQuantity(), _product.getBuyingPrice(), _product.getSellingPrice(), _product.getNation(), _product.getImageUrl(), _product.getDiscount());
        this.setCategory(Category.MOVIE_DISC);

        this.setListActors(new ArrayList<String>());
        this.setDirector("");

        this.setLanguage(Language.ENGLISH);
        this.setSubtitle(Language.VIETNAMESE);
        this.setGenre(MovieGenre.Action);
        this.setLength(0);
        this.setImdbPoint(0.0f);
        this.setPublicDate(LocalDateTime.now().toLocalDate());
    }

    public MovieDisc(String _productID, String _name, Enum<Category> _category, Enum<Status> _status, int _quantity, double _buyingPrice, double _sellingPrice, Enum<Nation> _nation, String _imageUrl, int _discount, ArrayList<String> _listActors, String _director, Enum<Language> _language,  Enum<Language> _subtitle, Enum<MovieGenre> _genre, int _length, float _imdbPoint, LocalDate _publicDate)
    {
        super(_productID, _name, _category, _status, _quantity, _buyingPrice, _sellingPrice, _nation, _imageUrl, _discount);
        this.setCategory(Category.MOVIE_DISC);

        this.setListActors(_listActors);
        this.setDirector(_director);

        this.setLanguage(_language);
        this.setSubtitle(_subtitle);
        this.setGenre(_genre);
        this.setLength(_length);
        this.setImdbPoint(_imdbPoint);
        this.setPublicDate(_publicDate);

    }

    public ArrayList<String> getListActors() {
        return listActors;
    }

    public void setListActors(ArrayList<String> listActors) {
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

    public LocalDate getPublicDate() {
        return publicDate;
    }

    public void setPublicDate(LocalDate publicDate) {
        this.publicDate = publicDate;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public void printDetail()
    {
        super.printDetail();
        System.out.println("Actors: "+getListActors());
        System.out.println("Director: "+getDirector());
        System.out.println("Language: "+ getLanguage().toString());
        System.out.println("Subtitle: "+getSubtitle().toString());
        System.out.println("Genre: "+getGenre().toString());
        System.out.println("Length: "+getLength());
        System.out.println("Point: "+getImdbPoint());
        System.out.println("Date: "+getPublicDate().toString());
    }

    public String toString()
    {
        String product = super.toString();
        product = product.concat("|");
        product = product.concat(Integer.toString(getListActors().size()));
        product = product.concat("|");

        for(int i = 0; i < getListActors().size(); i++)
        {
            product = product.concat(getListActors().get(i));
            product = product.concat("|");
        }

        product = product.concat(getDirector());
        product = product.concat("|");

        product = product.concat(getLanguage().toString());
        product = product.concat("|");

        product = product.concat(getSubtitle().toString());
        product = product.concat("|");

        product = product.concat(getGenre().toString());
        product = product.concat("|");

        product = product.concat(Integer.toString(getLength()));
        product = product.concat("|");

        product = product.concat(Float.toString(getImdbPoint()));
        product = product.concat("|");

        product = product.concat(getPublicDate().toString());

        return product;
    }

    public static MovieDisc valueOf(String line)
    {
        String[] parts = line.split(Pattern.quote("|"));

        Product p = new Product();
        p.setProductID(parts[1]);
        p.setName(parts[2]);
        p.setCategory(Category.valueOf(parts[0]));
        p.setStatus(Status.valueOf(parts[3]));
        p.setQuantity(Integer.valueOf(parts[4]));
        p.setBuyingPrice(Double.valueOf(parts[5]));
        p.setSellingPrice(Double.valueOf(parts[6]));
        p.setNation(Nation.valueOf(parts[7]));
        p.setImageUrl(parts[8]);
        p.setDiscount(Integer.valueOf(parts[9]));

        int curPos = 10;

        MovieDisc m = new MovieDisc(p);

        int nActors = Integer.valueOf(parts[curPos]);
        curPos += 1;
        for(int i = 0; i < nActors; i++)
        {
            m.getListActors().add(parts[curPos]);
            curPos += 1;
        }

        m.setDirector(parts[curPos]); curPos += 1;
        m.setLanguage(Language.valueOf(parts[curPos])); curPos += 1;
        m.setSubtitle(Language.valueOf(parts[curPos])); curPos += 1;
        m.setGenre(MovieGenre.valueOf(parts[curPos])); curPos += 1;
        m.setLength(Integer.valueOf(parts[curPos])); curPos += 1;
        m.setImdbPoint(Float.valueOf(parts[curPos])); curPos += 1;
        m.setPublicDate(LocalDate.parse(parts[curPos]));

        return m;
    }
}
