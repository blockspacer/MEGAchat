/*
 * (c) 2013-2015 by Mega Limited, Auckland, New Zealand
 *
 * This file is part of the MEGA SDK - Client Access Engine.
 *
 * Applications using the MEGA API must present a valid application key
 * and comply with the the rules set forth in the Terms of Service.
 *
 * The MEGA SDK is distributed in the hope that it will be useful,\
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * @copyright Simplified (2-clause) BSD License.
 * You should have received a copy of the license along with this
 * program.
 */
package nz.mega.sdk;


public interface MegaChatRoomListenerInterface {
    public void onChatRoomUpdate(MegaChatApiJava api, MegaChatRoom chat);
    public void onMessageLoaded(MegaChatApiJava api, MegaChatMessage msg);
    public void onMessageReceived(MegaChatApiJava api, MegaChatMessage msg);
    public void onMessageUpdate(MegaChatApiJava api, MegaChatMessage msg);
    public void onHistoryReloaded(MegaChatApiJava api, MegaChatRoom chat);
    public void onReactionUpdate(MegaChatApiJava api, long msgid, String reaction, int count);
}
