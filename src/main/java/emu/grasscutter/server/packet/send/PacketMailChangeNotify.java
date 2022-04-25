package emu.grasscutter.server.packet.send;


import emu.grasscutter.Grasscutter;
import emu.grasscutter.game.GenshinPlayer;
import emu.grasscutter.game.Mail;
import emu.grasscutter.net.packet.GenshinPacket;
import emu.grasscutter.net.packet.PacketOpcodes;
import emu.grasscutter.net.proto.*;

import java.util.ArrayList;
import java.util.List;

public class PacketMailChangeNotify extends GenshinPacket {

    public PacketMailChangeNotify(GenshinPlayer player, Mail message) {
        this (player, new ArrayList<Mail>(){{add(message);}});
    }

    public PacketMailChangeNotify(GenshinPlayer player, List<Mail> mailList) {
        super(PacketOpcodes.MailChangeNotify);

        MailChangeNotifyOuterClass.MailChangeNotify.Builder proto = MailChangeNotifyOuterClass.MailChangeNotify.newBuilder();

        for(Mail message : mailList) {
            MailTextContentOuterClass.MailTextContent.Builder mailTextContent = MailTextContentOuterClass.MailTextContent.newBuilder();
            mailTextContent.setTitle(message.mailContent.title);
            mailTextContent.setContent(message.mailContent.content);
            mailTextContent.setSender(message.mailContent.sender);

            List<MailItemOuterClass.MailItem> mailItems = new ArrayList<MailItemOuterClass.MailItem>();

            for(Mail.MailItem item : message.itemList) {
                MailItemOuterClass.MailItem.Builder mailItem = MailItemOuterClass.MailItem.newBuilder();
                ItemParamOuterClass.ItemParam.Builder itemParam = ItemParamOuterClass.ItemParam.newBuilder();
                itemParam.setItemId(item.itemId);
                itemParam.setCount(item.itemCount);
                mailItem.setItemParam(itemParam.build());

                mailItems.add(mailItem.build());
            }

            MailDataOuterClass.MailData.Builder mailData = MailDataOuterClass.MailData.newBuilder();
            mailData.setMailId(message._id);
            mailData.setMailTextContent(mailTextContent.build());
            mailData.addAllItemList(mailItems);
            mailData.setSendTime((int)message.sendTime);
            mailData.setExpireTime((int)message.expireTime);
            mailData.setImportance(message.importance);
            mailData.setIsRead(message.isRead);
            mailData.setIsAttachmentGot(message.isAttachmentGot);
            mailData.setStateValue(message.stateValue);

            proto.addMailList(mailData.build());

            Grasscutter.getLogger().info(Grasscutter.getDispatchServer().getGsonFactory().toJson(proto.build()));

            this.setData(proto.build());
        }
    }
}