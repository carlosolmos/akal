package com.metistd.akal;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MusicFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MusicFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MusicFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private boolean isPlaying = false;
    private MainActivity mainActivity = null;
    private ImageView musicIcon;
    private ImageView rewindIcon;
    private ImageView playPauseIcon;
    private ImageView forwardIcon;
    private TextView txtTrackName;

    public MusicFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MusicFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MusicFragment newInstance(String param1, String param2) {
        MusicFragment fragment = new MusicFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainActivity = (MainActivity)getActivity();
        // Inflate the layout for this fragment
        final View musicView = inflater.inflate(R.layout.fragment_music, container, false);
        txtTrackName = (TextView)musicView.findViewById(R.id.txtTrackName);
        musicIcon = (ImageView)musicView.findViewById(R.id.imgMusicIcon);
        musicIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("MusicFragment", "Toggle Music");
                if(!isPlaying){
                    playMusic(musicView);
                }else{
                    stopMusic(musicView);
                }
            }
        });

        rewindIcon = (ImageView) musicView.findViewById(R.id.imgRewind);
        rewindIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("MusicFragment", "Rewind Music");
                rewindMusic(view);
            }
        });

        playPauseIcon = (ImageView) musicView.findViewById(R.id.imgPause);
        playPauseIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isPlaying){
                    Log.d("MusicFragment", "Pause Music");
                    pauseMusic(view);
                }else{
                    Log.d("MusicFragment", "Resume Music");
                    resumeMusic(view);
                }
            }
        });


        forwardIcon = (ImageView) musicView.findViewById(R.id.imgForward);
        forwardIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("MusicFragment", "Forward Music");
                forwardMusic(view);
            }
        });

        return musicView;
    }

    public void rewindMusic(View view){
        if(mainActivity != null) {
            mainActivity.rewind();
            txtTrackName.setText(mainActivity.getTrackName());
        }
    }
    public void pauseMusic(View view){
        if(mainActivity != null) {
            isPlaying = false;
            mainActivity.pause();
            playPauseIcon.setImageResource(R.drawable.play);
            txtTrackName.setText(mainActivity.getTrackName());
        }
    }

    public void resumeMusic(View view){
        if(mainActivity != null) {
            isPlaying = true;
            mainActivity.resume();
            playPauseIcon.setImageResource(R.drawable.pause);
            txtTrackName.setText(mainActivity.getTrackName());
        }
    }

    public void forwardMusic(View view){
        if(mainActivity != null) {
            mainActivity.forward();
            txtTrackName.setText(mainActivity.getTrackName());
        }
    }

    public void playMusic(View view){
        if(mainActivity != null) {
            isPlaying = true;
            mainActivity.playAllTracks();
            musicIcon.setImageResource(R.drawable.notew);
            playPauseIcon.setImageResource(R.drawable.pause);
            txtTrackName.setText(mainActivity.getTrackName());
        }
    }

    public void stopMusic(View view){
        if(mainActivity != null) {
            isPlaying = false;
            mainActivity.stop();
            musicIcon.setImageResource(R.drawable.note);
            txtTrackName.setText("");
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    /*
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }*/

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
