package com.example.streamchatapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.example.streamchatapp.R
import com.example.streamchatapp.databinding.FragmentChatBinding
import com.getstream.sdk.chat.viewmodel.MessageInputViewModel
import com.getstream.sdk.chat.viewmodel.messages.MessageListViewModel
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.channel.subscribeFor
import io.getstream.chat.android.client.events.TypingStartEvent
import io.getstream.chat.android.client.events.TypingStopEvent
import io.getstream.chat.android.client.models.name
import io.getstream.chat.android.ui.message.input.viewmodel.bindView
import io.getstream.chat.android.ui.message.list.header.viewmodel.MessageListHeaderViewModel
import io.getstream.chat.android.ui.message.list.header.viewmodel.bindView
import io.getstream.chat.android.ui.message.list.viewmodel.bindView
import io.getstream.chat.android.ui.message.list.viewmodel.factory.MessageListViewModelFactory

class ChatFragment : Fragment() {

    private val args: ChatFragmentArgs by navArgs()

    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatBinding.inflate(inflater, container, false)

        setupMessages()

        binding.messageListHeaderView.setBackButtonClickListener {
            requireActivity().onBackPressed()
        }

        return binding.root
    }

    private fun setupMessages() {
        val factory = MessageListViewModelFactory(cid = args.channelId)

        val messageListHeaderViewModel: MessageListHeaderViewModel by viewModels { factory }
        val messageListViewModel: MessageListViewModel by viewModels { factory }
        val messageInputViewModel: MessageInputViewModel by viewModels { factory }

        messageListHeaderViewModel.bindView(binding.messageListHeaderView, viewLifecycleOwner)
        messageListViewModel.bindView(binding.messageListView, viewLifecycleOwner)
        messageInputViewModel.bindView(binding.messageInputView, viewLifecycleOwner)


        val nobodyTyping = "nobody is typing"
        binding.typingHeaderView.text = nobodyTyping

        val currentlyTyping = mutableSetOf<String>()

        ChatClient
            .instance()
            .channel(args.channelId)
            .subscribeFor(
                this, TypingStartEvent::class, TypingStopEvent::class
            ) { event ->
                when (event) {
                    is TypingStartEvent -> currentlyTyping.add(event.user.name)
                    is TypingStopEvent -> currentlyTyping.remove(event.user.name)
                    else -> {}
                }

                binding.typingHeaderView.text = when {
                    currentlyTyping.isNotEmpty() -> currentlyTyping.toString().replace("[","").replace("","").replace("]","")+" is typing"

                    else -> nobodyTyping
                }
            }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}