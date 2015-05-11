package com.example.lovetalk.service;

import com.avos.avoscloud.*;
import com.example.lovetalk.chat.db.DBMsg;
import com.example.lovetalk.chat.entity.Msg;
import com.example.lovetalk.chat.entity.MsgBuilder;
import com.example.lovetalk.chat.entity.RoomType;

import java.io.IOException;

/**
 * Created by lzw on 14/11/23.
 */
public class MsgAgent {
  RoomType roomType;
  String toId;

  public MsgAgent(RoomType roomType, String toId) {
    this.roomType = roomType;
    this.toId = toId;
  }

  public interface MsgBuilderHelper {
    void specifyType(MsgBuilder msgBuilder);
  }

  public Msg createAndSendMsg(MsgBuilderHelper msgBuilderHelper) throws IOException, AVException {
    MsgBuilder builder = new MsgBuilder();
    builder.target(roomType, toId);
    msgBuilderHelper.specifyType(builder);
    builder.upload();
    Msg msg = builder.build();
    sendMsg(msg);
    DBMsg.insertMsg(msg);
    return msg;
  }

  public Msg sendMsg(Msg msg) {
    AVMessage avMsg = msg.toAVMessage();
    Session session = ChatService.getSession();
    if (roomType == RoomType.Single) {
      session.sendMessage(avMsg);
    } else {
      Group group = session.getGroup(toId);
      group.sendMessage(avMsg);
    }
    return msg;
  }

}
