<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    public function up(): void
    {
        Schema::create('event_notifications', function (Blueprint $table) {
            $table->id();
            $table->unsignedBigInteger('event_id');
            $table->unsignedBigInteger('user_id');
            $table->enum('notification_type', [
                'event_started', 'event_ended', 'reward_earned', 'milestone_reached'
            ]);
            $table->string('title', 255);
            $table->longText('message');
            $table->boolean('is_read')->default(false);
            $table->dateTime('created_at')->useCurrent();
            $table->dateTime('read_at')->nullable();

            $table->index('user_id');
            $table->index('event_id');
            $table->index('is_read');
            $table->foreign('event_id')->references('id')->on('events')->onDelete('cascade');
            $table->foreign('user_id')->references('id')->on('users')->onDelete('cascade');
        });
    }

    public function down(): void
    {
        Schema::dropIfExists('event_notifications');
    }
};
