<?php

namespace App\Http\Controllers\Api\V1;

use App\Models\Event;
use App\Models\EventStatistic;
use Illuminate\Http\Request;

class EventController extends Controller
{
    /**
     * Get All Events with Filtering
     */
    public function index(Request $request)
    {
        $query = Event::query();

        if ($request->has('type')) {
            $query->where('event_type', $request->type);
        }

        if ($request->has('is_active')) {
            $query->where('is_active', $request->boolean('is_active'));
        }

        if ($request->has('status')) {
            $status = $request->status;
            if ($status === 'upcoming') {
                $query->upcoming();
            } elseif ($status === 'ongoing') {
                $query->ongoing();
            } elseif ($status === 'completed') {
                $query->completed();
            }
        }

        $events = $query->paginate($request->get('per_page', 20));

        return response()->json([
            'success' => true,
            'data' => $events->items(),
            'pagination' => [
                'current_page' => $events->currentPage(),
                'per_page' => $events->perPage(),
                'total' => $events->total(),
                'total_pages' => $events->lastPage(),
            ],
        ]);
    }

    /**
     * Create Event
     */
    public function store(Request $request)
    {
        $validated = $request->validate([
            'event_type' => 'required|in:' . implode(',', Event::getEventTypes()),
            'name' => 'required|string|max:255',
            'description' => 'nullable|string',
            'banner_image_url' => 'nullable|url',
            'icon_url' => 'nullable|url',
            'event_rules' => 'nullable|string',
            'start_date' => 'required|date',
            'end_date' => 'required|date|after:start_date',
            'is_active' => 'boolean',
        ]);

        $user = auth()->user();

        if (!$user->canCreateEvent()) {
            return response()->json([
                'success' => false,
                'message' => 'Unauthorized to create events',
            ], 403);
        }

        $validated['created_by'] = $user->id;

        $event = Event::create($validated);

        // Create statistics record
        EventStatistic::create(['event_id' => $event->id]);

        return response()->json([
            'success' => true,
            'message' => 'Event created successfully',
            'data' => $event,
        ], 201);
    }

    /**
     * Get Event Details
     */
    public function show($id)
    {
        $event = Event::with('rewards', 'statistics')->findOrFail($id);

        return response()->json([
            'success' => true,
            'data' => [
                'id' => $event->id,
                'event_type' => $event->event_type,
                'name' => $event->name,
                'description' => $event->description,
                'banner_image_url' => $event->banner_image_url,
                'icon_url' => $event->icon_url,
                'event_rules' => $event->event_rules,
                'start_date' => $event->start_date,
                'end_date' => $event->end_date,
                'is_active' => $event->is_active,
                'is_published' => $event->is_published,
                'status' => $event->getStatus(),
                'days_remaining' => $event->days_remaining,
                'rewards' => $event->rewards,
                'statistics' => $event->statistics,
            ],
        ]);
    }

    /**
     * Update Event
     */
    public function update(Request $request, $id)
    {
        $event = Event::findOrFail($id);

        $user = auth()->user();
        if (!$user->canEditEvent($event)) {
            return response()->json([
                'success' => false,
                'message' => 'Unauthorized to edit this event',
            ], 403);
        }

        $validated = $request->validate([
            'name' => 'string|max:255',
            'description' => 'nullable|string',
            'banner_image_url' => 'nullable|url',
            'icon_url' => 'nullable|url',
            'event_rules' => 'nullable|string',
            'start_date' => 'date',
            'end_date' => 'date|after:start_date',
            'is_active' => 'boolean',
        ]);

        $event->update($validated);

        return response()->json([
            'success' => true,
            'message' => 'Event updated successfully',
            'data' => $event,
        ]);
    }

    /**
     * Toggle Event Status
     */
    public function toggleStatus($id)
    {
        $event = Event::findOrFail($id);

        $user = auth()->user();
        if (!$user->canEditEvent($event)) {
            return response()->json([
                'success' => false,
                'message' => 'Unauthorized',
            ], 403);
        }

        if ($event->is_active) {
            $event->deactivate();
        } else {
            $event->activate();
        }

        return response()->json([
            'success' => true,
            'message' => 'Event status updated',
            'data' => ['is_active' => $event->is_active],
        ]);
    }

    /**
     * Delete Event
     */
    public function destroy($id)
    {
        $event = Event::findOrFail($id);

        $user = auth()->user();
        if (!$user->canDeleteEvent($event)) {
            return response()->json([
                'success' => false,
                'message' => 'Unauthorized',
            ], 403);
        }

        $event->delete();

        return response()->json([
            'success' => true,
            'message' => 'Event deleted successfully',
        ]);
    }
}
