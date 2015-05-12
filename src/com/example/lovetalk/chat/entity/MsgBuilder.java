package com.example.lovetalk.chat.entity;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.example.lovetalk.service.ChatService;
import com.example.lovetalk.util.AVOSUtils;
import com.example.lovetalk.util.PathUtils;
import com.example.lovetalk.util.Utils;

import java.io.IOException;

public class MsgBuilder {
	Msg msg;

	public MsgBuilder() {
		msg = new Msg();
	}

	public void target(RoomType roomType, String toId) {
		String convid;
		msg.setRoomType(roomType);
		if (roomType == RoomType.Single) {
			msg.setToPeerId(toId);
			msg.setRequestReceipt(true);
			convid = AVOSUtils.convid(ChatService.getSelfId(), toId);
		} else {
			convid = toId;
		}
		msg.setConvid(convid);
	}

	public void text(String content) {
		msg.setType(Msg.Type.Text);
		msg.setContent(content);
	}

	private void file(Msg.Type type, String objectId) {
		msg.setType(type);
		msg.setObjectId(objectId);
	}

	public void image(String objectId) {
		file(Msg.Type.Image, objectId);
	}

	public void location(String address, double latitude, double longitude) {
		String content = address + "&" + latitude + "&" + longitude;
		msg.setContent(content);
		msg.setType(Msg.Type.Location);
	}

	public void audio(String objectId) {
		file(Msg.Type.Audio, objectId);
	}

	public Msg build() {
		msg.setStatus(Msg.Status.SendStart);
		msg.setTimestamp(System.currentTimeMillis());
		msg.setFromPeerId(ChatService.getSelfId());
		if (msg.getObjectId() == null) {
			msg.setObjectId(Utils.myUUID());
		}
		return msg;
	}

	public void upload() throws IOException, AVException {
		if (msg.getType() != Msg.Type.Audio && msg.getType() != Msg.Type.Image) {
			return;
		}
		String objectId = msg.getObjectId();
		if (objectId == null) {
			throw new NullPointerException("objectId mustn't be null");
		}
		String filePath = PathUtils.getChatFilePath(objectId);
		AVFile file = AVFile.withAbsoluteLocalPath(objectId, filePath);
		file.save();
		String url = file.getUrl();
		msg.setContent(url);
	}
}