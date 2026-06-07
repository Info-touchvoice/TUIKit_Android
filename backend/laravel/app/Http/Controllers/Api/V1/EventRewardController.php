<?php

namespace App\Http\Controllers\Api\V1;

use App\Models\Event;
use App\Models\EventReward;
use Illuminate\Http\Request;

class EventRewardController extends Controller
{
    /**
     * Get Event Rewards
     */
    public function index($eventId)
    {
        $event = Event::findOrFail($eventId);
        $rewards = EventReward::where('event_id', $eventId)->get();

        return response()->json([
            'success' => true,
            'data' => $rewards,
        ]);
    }

    /**
     * Create Event Reward
     */
    public function store(Request $request, $eventId)
    {
        $event = Event::findOrFail($eventId);

        $user = auth()->user();
        if (!$user->canEditEvent($event)) {
            return response()->json([
                'success' => false,
                'message' => 'Unauthorized',
            ], 403);
        }

        $validated = $request->validate([
            'reward_tier' => 'required|integer|min:1',
            'reward_type' => 'required|in:' . implode(',', EventReward::getRewardTypes()),
            'reward_amount' => 'required|integer|min:1',
            'reward_name' => 'nullable|string|max:255',
            'reward_description' => 'nullable|string',
            'trigger_type' => 'required|in:' . implode(',', EventReward::getTriggerTypes()),
            'trigger_condition' => 'required|integer|min:1',
            'max_claims' => 'nullable|integer|min:1',
        ]);

        $validated['event_id'] = $eventId;
        $reward = EventReward::create($validated);

        return response()->json([
            'success' => true,
            'message' => 'Reward created successfully',
            'data' => $reward,
        ], 201);
    }

    /**
     * Update Event Reward
     */
    public function update(Request $request, $eventId, $rewardId)
    {
        $event = Event::findOrFail($eventId);
        $reward = EventReward::findOrFail($rewardId);

        $user = auth()->user();
        if (!$user->canEditEvent($event)) {
            return response()->json([
                'success' => false,
                'message' => 'Unauthorized',
            ], 403);
        }

        $validated = $request->validate([
            'reward_tier' => 'integer|min:1',
            'reward_type' => 'in:' . implode(',', EventReward::getRewardTypes()),
            'reward_amount' => 'integer|min:1',
            'reward_name' => 'nullable|string|max:255',
            'reward_description' => 'nullable|string',
            'trigger_type' => 'in:' . implode(',', EventReward::getTriggerTypes()),
            'trigger_condition' => 'integer|min:1',
            'max_claims' => 'nullable|integer|min:1',
        ]);

        $reward->update($validated);

        return response()->json([
            'success' => true,
            'message' => 'Reward updated successfully',
            'data' => $reward,
        ]);
    }

    /**
     * Delete Event Reward
     */
    public function destroy($eventId, $rewardId)
    {
        $event = Event::findOrFail($eventId);
        $reward = EventReward::findOrFail($rewardId);

        $user = auth()->user();
        if (!$user->canEditEvent($event)) {
            return response()->json([
                'success' => false,
                'message' => 'Unauthorized',
            ], 403);
        }

        $reward->delete();

        return response()->json([
            'success' => true,
            'message' => 'Reward deleted successfully',
        ]);
    }
}
