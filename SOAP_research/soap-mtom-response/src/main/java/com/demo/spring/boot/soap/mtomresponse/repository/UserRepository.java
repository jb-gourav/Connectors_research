package com.demo.spring.boot.soap.mtomresponse.repository;

import org.springframework.stereotype.Component;

import com.demo.spring.boot.soap.mtomresponse.models.user.ProfilePicture;
import com.demo.spring.boot.soap.mtomresponse.models.user.User;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;

import java.awt.Image;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class UserRepository {

    private static final String PROFILE_PICTURE_PATH = "D:\\Connectors_research\\SOAP_research\\soap-mtom-response\\src\\main\\resources\\mtom\\";
    private static final Map<Integer, User> USER_MAP = new HashMap<>();

    @PostConstruct
    public void init() {
    	List<Image> contentList = new ArrayList<>();
        User user = new User();
        ProfilePicture pic = new ProfilePicture();
        user.setId(1);
        user.setFirstname("first");
        user.setLastname("image");
        try {
            pic.setName(user.getId() + "-" + user.getFirstname() + ".jpeg");
            contentList.add(ImageIO.read(new File(PROFILE_PICTURE_PATH + user.getId() + ".jpg")));
            contentList.add(ImageIO.read(new File(PROFILE_PICTURE_PATH + 2 + ".jpg")));
            pic.getContent().addAll(contentList);
            user.setProfilePicture1(pic);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        USER_MAP.put(user.getId(), user);
    }

    public User getUserById(int id) {
        return USER_MAP.get(id);
    }
}
