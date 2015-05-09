package br.com.mpro3.MproEntity.model;

import br.com.mpro3.MproEntity.Entity;
import br.com.mpro3.MproEntity.Id;

/**
 * Created by matheus on 23/03/15.
 */

@Entity
public class User
{
    @Id
    private int cod;
    private String name;
    private String nickName;
    private String birthDate;

    public User(){}

    public User(String name, String nickName, String birthDate)
    {
        this.name = name;
        this.nickName = nickName;
        this.birthDate = birthDate;
    }

    public int getCod() {
        return cod;
    }

    public void setCod(int cod) {
        this.cod = cod;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public String getNickName() {
        return nickName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }
}
