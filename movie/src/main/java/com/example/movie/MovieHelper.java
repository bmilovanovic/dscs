package com.example.movie;

import android.text.TextUtils;

import com.example.movie.tables.Country;
import com.example.movie.tables.Film;
import com.example.movie.tables.FilmCountry;
import com.example.movie.tables.FilmGenre;
import com.example.movie.tables.FilmPersonRole;
import com.example.movie.tables.Genre;
import com.example.movie.tables.Person;
import com.example.movie.tables.Role;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceList;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.table.query.Query;
import com.microsoft.windowsazure.mobileservices.table.query.QueryOperations;
import com.microsoft.windowsazure.mobileservices.table.query.QueryOrder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.microsoft.windowsazure.mobileservices.table.query.QueryOperations.val;

/**
 * Helper class for parsing and storing Movie
 */
public class MovieHelper {

    private static ArrayList<Genre> sGenres = new ArrayList<>();
    private static ArrayList<Country> sCountries = new ArrayList<>();
    private static ArrayList<Role> sRoles = new ArrayList<>();

    static Genre getGenre(MobileServiceClient client, String genreName)
            throws ExecutionException, InterruptedException {
        // Try to find genreName in local genre list
        for (Genre genre : sGenres) {
            if (TextUtils.equals(genreName, genre.getGenreName())) {
                return genre;
            }
        }

        // Fetch the newest version of the genres, maybe someone added a new one
        sGenres = client.getTable(Genre.class)
                .where().orderBy("genreId", QueryOrder.Ascending).execute().get();
        for (Genre genre : sGenres) {
            if (TextUtils.equals(genreName, genre.getGenreName())) {
                return genre;
            }
        }

        // There is no an existing genre with a genreName, so add a new one
        Genre newGenre = new Genre();
        newGenre.setGenreName(genreName);

        int highestGenreId = -1;
        if (!sGenres.isEmpty()) {
            highestGenreId = sGenres.get(sGenres.size() - 1).getGenreId();
        }
        newGenre.setGenreId(highestGenreId + 1);

        // Insert new genre both in azure table and a local static array list
        client.getTable(Genre.class).insert(newGenre).get();
        sGenres.add(newGenre);

        return newGenre;
    }

    static Country getCountry(MobileServiceClient client, String countryName)
            throws ExecutionException, InterruptedException {
        // Try to find countryName in local country list
        if (sCountries.isEmpty()) {
            sCountries = client.getTable(Country.class)
                    .where().orderBy("countryId", QueryOrder.Ascending).execute().get();
        }
        for (Country country : sCountries) {
            if (TextUtils.equals(countryName, country.getCountryName())) {
                return country;
            }
        }

        // There is no an existing country with a countryName, so add a new one
        Country newCountry = new Country();
        newCountry.setCountryName(countryName);

        int highestCountryId = -1;
        if (!sCountries.isEmpty()) {
            highestCountryId = sCountries.get(sCountries.size() - 1).getCountryId();
        }
        newCountry.setCountryId(highestCountryId + 1);

        // Insert new country both in azure table and a local static array list
        client.getTable(Country.class).insert(newCountry);
        sCountries.add(newCountry);

        return newCountry;
    }

    static Role getRole(MobileServiceClient client, String roleName)
            throws ExecutionException, InterruptedException {
        // Try to find roleName in local role list
        if (sRoles.isEmpty()) {
            sRoles = client.getTable(Role.class)
                    .where().orderBy("roleId", QueryOrder.Ascending).execute().get();
        }
        for (Role role : sRoles) {
            if (TextUtils.equals(roleName, role.getRoleName())) {
                return role;
            }
        }

        // There is no an existing role with a roleName, so add a new one
        Role newRole = new Role();
        newRole.setRoleName(roleName);

        int highestRoleId = -1;
        if (!sRoles.isEmpty()) {
            highestRoleId = sRoles.get(sRoles.size() - 1).getRoleId();
        }
        newRole.setRoleId(highestRoleId + 1);

        // Insert new role both in azure table and a local static array list
        client.getTable(Role.class).insert(newRole);
        sRoles.add(newRole);

        return newRole;
    }

    static Person getPerson(MobileServiceClient client, String personName) throws
            ExecutionException, InterruptedException {
        // Fetch all the persons from the azure table
        MobileServiceTable<Person> personTable = client.getTable(Person.class);
        Query query = QueryOperations.field("name").eq(val(personName));
        MobileServiceList<Person> persons = personTable.where(query).execute().get();
        if (!persons.isEmpty()) {
            return persons.get(0);
        }

        // There's no such person so the new one needs to be inserted.
        Person person = new Person();
        person.setName(personName);

        persons = personTable.where().orderBy("personId", QueryOrder.Ascending).execute().get();
        int highestPersonId = -1;
        if (persons.size() > 0) {
            highestPersonId = persons.get(persons.size() - 1).getPersonId();
        }
        person.setPersonId(highestPersonId + 1);

        personTable.insert(person);

        return person;
    }

    public static List<Class> getAllDomainClasses() {
        ArrayList<Class> list = new ArrayList<>();

        list.add(Country.class);
        list.add(Film.class);
        list.add(FilmCountry.class);
        list.add(FilmGenre.class);
        list.add(FilmPersonRole.class);
        list.add(Genre.class);
        list.add(Person.class);
        list.add(Role.class);

        return list;
    }
}
