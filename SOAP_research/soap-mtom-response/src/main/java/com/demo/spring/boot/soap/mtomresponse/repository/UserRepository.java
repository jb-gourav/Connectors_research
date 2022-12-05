package com.demo.spring.boot.soap.mtomresponse.repository;

import com.demo.spring.boot.soap.mtomresponse.models.user.ProfilePicture;
import com.demo.spring.boot.soap.mtomresponse.models.user.UploadUserRequest;
import com.demo.spring.boot.soap.mtomresponse.models.user.UploadUserResponse;
import com.demo.spring.boot.soap.mtomresponse.models.user.User;

import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Component;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.activation.DataHandler;
import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;

@Component
public class UserRepository {

  private static final String PROFILE_PICTURE_PATH = "D:\\jitterbit\\mtom";
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
      contentList.add(ImageIO.read(new File(PROFILE_PICTURE_PATH + "//" + user.getId() + ".jpg")));
      contentList.add(ImageIO.read(new File(PROFILE_PICTURE_PATH + "//" + 2 + ".jpg")));
      pic.getContent().addAll(contentList);
      user.setProfilePicture1(pic);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    USER_MAP.put(user.getId(), user);
  }

  public UploadUserResponse uploadUserRepo(UploadUserRequest userRequest) {
    UploadUserResponse response = new UploadUserResponse();
    int id = userRequest.getId();
    DataHandler handler = userRequest.getContent();
    String contentType = handler.getContentType();
    File file = new File(PROFILE_PICTURE_PATH + "//" + id + ".jpg");
    response.setId(userRequest.getId());
    // commons-io
    try {
      FileUtils.copyInputStreamToFile(handler.getInputStream(), file);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    return response;
  }

  public User getUserById(int id) {
    return USER_MAP.get(id);
  }
}
