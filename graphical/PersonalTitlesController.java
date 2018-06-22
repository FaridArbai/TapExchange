package com.faridarbai.tapexchange.graphical;

import android.support.design.widget.AppBarLayout;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;

public class PersonalTitlesController implements AppBarLayout.OnOffsetChangedListener{
	private static final float TITLE_VISIBILITY_THRESHOLD = 0.9f;
	private static final float STATUS_VISIBILITY_THRESHOLD = 0.3f;
	private static final int TRANSITION_DURATION = 250;
	
	private LinearLayout status;
	private TextView title;
	private boolean status_is_visible;
	private boolean title_is_visible;
	
	public PersonalTitlesController(LinearLayout status, TextView title){
		this.status = status;
		this.title = title;
		
		this.setInvisible(title);
	}
	
	protected PersonalTitlesController(){}
	
	@Override
	public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
		int max_scroll = appBarLayout.getTotalScrollRange();
		float scroll_factor = (float) Math.abs(verticalOffset) / (float) max_scroll;
		
		this.handleTitleVisibility(scroll_factor);
		this.handleStatusVisibility(scroll_factor);
	}
	
	
	private void handleTitleVisibility(float scroll_factor){
		boolean is_visible = this.title_is_visible;
		View title = this.title;
		
		if (scroll_factor > TITLE_VISIBILITY_THRESHOLD){
			if(!is_visible){
				this.setVisible(title);
			}
		}
		else{
			if(is_visible){
				this.setInvisible(title);
			}
		}
	}
	
	private void handleStatusVisibility(float scroll_factor){
		boolean is_visible = this.status_is_visible;
		View status = this.status;
		
		if (scroll_factor < STATUS_VISIBILITY_THRESHOLD){
			if(!is_visible){
				this.setVisible(status);
			}
		}
		else{
			if(is_visible){
				this.setInvisible(status);
			}
		}
	}
	
	public static void startAlphaTransition(View view, int visibility){
		boolean will_be_visible = (visibility==View.VISIBLE);
		AlphaAnimation transition = will_be_visible ? (new AlphaAnimation(0f, 1f)):(new AlphaAnimation(1f, 0f));
		
		transition.setDuration(TRANSITION_DURATION);
		transition.setFillAfter(true);
		
		view.startAnimation(transition);
	}
	
	
	
	private void setVisible(View view){
		if(view == status){
			this.status_is_visible = true;
		}
		else if (view == title){
			this.title_is_visible = true;
		}
		
		this.startAlphaTransition(view, View.VISIBLE);
	}
	
	private void setInvisible(View view){
		if(view == status){
			this.status_is_visible = false;
		}
		else if (view == title){
			this.title_is_visible = false;
		}
		
		this.startAlphaTransition(view, View.INVISIBLE);
	}

	private boolean isVisible(View view){
		boolean is_visible = false;
		
		if(view == status){
			is_visible = this.status_is_visible;
		}
		else if (view == title){
			is_visible = this.title_is_visible;
		}
		
		return is_visible;
	}


























































}