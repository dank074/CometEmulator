package com.habboproject.server.network.messages.incoming.group.forum.threads;

import com.habboproject.server.boot.Comet;
import com.habboproject.server.game.groups.GroupManager;
import com.habboproject.server.game.groups.types.Group;
import com.habboproject.server.game.groups.types.components.forum.settings.ForumPermission;
import com.habboproject.server.game.groups.types.components.forum.settings.ForumSettings;
import com.habboproject.server.game.groups.types.components.forum.threads.ForumThread;
import com.habboproject.server.game.groups.types.components.forum.threads.ForumThreadReply;
import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.messages.outgoing.group.forums.GroupForumUpdateReplyMessageComposer;
import com.habboproject.server.network.messages.outgoing.notification.NotificationMessageComposer;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;
import com.habboproject.server.storage.queries.groups.GroupForumThreadDao;

/**
 * Created by brend on 31/01/2017.
 */
public class HideReplyMessageEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        int groupId = msg.readInt();
        int threadId = msg.readInt();
        int messageId = msg.readInt();
        int state = msg.readInt();

        Group group = GroupManager.getInstance().get(groupId);
        if (group == null || !group.getData().hasForum()) {
            return;
        }

        ForumSettings forumSettings = group.getForumComponent().getForumSettings();
        if (forumSettings.getModeratePermission() == ForumPermission.OWNER ? client.getPlayer().getId() != group.getData().getId() : !group.getMembershipComponent().getAdministrators().contains(client.getPlayer().getId())) {
            return;
        }

        ForumThread forumThread = group.getForumComponent().getForumThreads().get(threadId);
        if (forumThread == null) {
            return;
        }

        ForumThreadReply reply = forumThread.getReplyById(messageId);
        if (reply == null) {
            return;
        }

        reply.setState(state);

        reply.setDeleterId(state == 1 ? 0 : client.getPlayer().getId());
        reply.setDeleterTime(state == 1 ? 0 : (int) Comet.getTime());

        GroupForumThreadDao.saveMessageState(reply.getId(), state, state == 1 ? 0 : client.getPlayer().getId(), state == 1 ? 0 : (int) Comet.getTime());

        client.send(new NotificationMessageComposer("forums.message." + (state != 1 ? "hidden" : "restored")));
        client.send(new GroupForumUpdateReplyMessageComposer(reply, threadId, groupId));
    }
}
